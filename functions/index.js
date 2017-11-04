'use strict';

const functions = require('firebase-functions'),
  admin = require('firebase-admin'),
  logging = require('@google-cloud/logging')();

admin.initializeApp(functions.config().firebase);

let TOKEN_URI = 'https://connect.stripe.com/oauth/token';
let AUTHORIZE_URI = 'https://connect.stripe.com/oauth/authorize';
const stripe = require('stripe')("sk_live_CkXUB4FiJlJBSK4VtlCxuFtT"),
  currency = functions.config().stripe.currency || 'CAD';

exports.updateConnected = functions.database.ref("tutors/{userId}/stripe_connected/update").onWrite(event =>{
  let data = event.data.val();
  return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected/id`).once('value').then(snapshot => {
    return snapshot.val();
  }).then(stripeId => {
   return  stripe.accounts.update(stripeId,data);
  }).then(response => {
      // If the result is successful, write it back to the database
      return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected`).set(response);
    }, error => {
      // We want to capture errors and render them in a user-friendly way, while
      // still logging an exception with Stackdriver
      return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected`).child('error').set(userFacingMessage(error)).then(() => {
        return reportError(error, {user: event.params.userId});
      });
    }
  );
});

exports.createExternalAccount = functions.database.ref("tutors/{userId}/stripe_connected/update/bank_account").onWrite(event =>{
  let data = event.data.val();
  return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected/id`).once('value').then(snapshot => {
    return snapshot.val();
  }).then(stripeId => {
    let externalAccount = {
      external_account: data
    };
    return  stripe.accounts.createExternalAccount(stripeId,externalAccount);
  }).then(response => {
      // If the result is successful, write it back to the database
      return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected`).set(response);
    }, error => {
      // We want to capture errors and render them in a user-friendly way, while
      // still logging an exception with Stackdriver
      return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected`).child('error').set(userFacingMessage(error)).then(() => {
        return reportError(error, {user: event.params.userId});
      });
    }
  );
});

// [START chargecustomer]
// Charge the Stripe customer whenever an amount is written to the Realtime database
exports.createStripeCharge = functions.database.ref('/users/{userId}/charges/{id}').onWrite(event => {
  const val = event.data.val();
  // This onWrite will trigger whenever anything is written to the path, so
  // noop if the charge was deleted, errored out, or the Stripe API returned a result (id exists)
  if (val === null || val.id || val.error) return null;
  // Look up the Stripe customer id written in createStripeCustomers
  return admin.database().ref(`/users/${event.params.userId}/customer_id`).once('value').then(snapshot => {
    return snapshot.val();s
  }).then(customer => {
    // Create a charge using the pushId as the idempotency key, protecting against double charges
    const amount = val.amount;
    const destination = val.destination;
    const payout = Math.round(amount*0.85);
    const idempotency_key = event.params.id;
    let charge = {
      amount,
      currency,
      customer,
      destination:{
        account: destination,
        amount: payout
      }
    };
    if (val.source !== null) charge.source = val.source;
    return stripe.charges.create(charge, {idempotency_key});
  }).then(response => {
      // If the result is successful, write it back to the database
      return event.data.adminRef.set(response);
    }, error => {
      // We want to capture errors and render them in a user-friendly way, while
      // still logging an exception with Stackdriver
      return event.data.adminRef.child('error').set(userFacingMessage(error)).then(() => {
        return reportError(error, {user: event.params.userId});
      });
    }
  );
});
// [END chargecustomer]]


// When a user is created, register them with Stripe
exports.createStripeCustomers = functions.database.ref('/users/{userId}').onCreate(event => {
  const data = event.data.val();
  return stripe.customers.create({
    email: data.email,
  }).then(customer => {
    return admin.database().ref(`/users/${data.id}/customer_id`).set(customer.id);
  });
});

// When a user is created, register them with Stripe
exports.createStripeConnectedAccount = functions.database.ref("/tutors/{userId}").onCreate(event => {
  const data = event.data.val();
  return stripe.accounts.create({
    country: 'CA',
    type: 'custom'
  }).then(account => {
    return admin.database().ref(`/tutors/${event.params.userId}/stripe_connected`).update(account);
  });
});

// Add a payment source (bank account) for a user by writing a stripe payment source token to Realtime database
exports.addBankAccount = functions.database.ref('/tutors/{userId}/paymentInfo/{pushId}/token').onWrite(event => {
  const source = event.data.val();
  if (source === null) return null;
  return admin.database().ref(`/users/${event.params.userId}/customer_id`).once('value').then(snapshot => {
    return snapshot.val();
  }).then(customer => {
    return stripe.customers.createSource(customer, {source});
  }).then(response => {
    return event.data.adminRef.parent.set(response);
  }, error => {
    return event.data.adminRef.parent.child('error').set(userFacingMessage(error)).then(() => {
      return reportError(error, {user: event.params.userId});
    });
  });
});


// Add a payment source (card) for a user by writing a stripe payment source token to Realtime database
exports.addPaymentSource = functions.database.ref('/users/{userId}/paymentInfo/{pushId}/token').onWrite(event => {
  const source = event.data.val();
  if (source === null) return null;
  return admin.database().ref(`/users/${event.params.userId}/customer_id`).once('value').then(snapshot => {
    return snapshot.val();
  }).then(customer => {
    return stripe.customers.createSource(customer, {source});
  }).then(response => {
    return event.data.adminRef.parent.set(response);
  }, error => {
    return event.data.adminRef.parent.child('error').set(userFacingMessage(error)).then(() => {
      return reportError(error, {user: event.params.userId});
    });
  }
  );
});

exports.createRefund = functions.database.ref('/users/{userId}/refunds/{pushId}/').onWrite(event => {
  const data = event.data.val();

  return stripe.refunds.create({
    charge: data.chargeId,
    reverse_transfer: true
  }).then(response => {
    return event.data.adminRef.set(response);
  }, error =>{
    // asynchronously called
    return event.data.adminRef.child('error').set(userFacingMessage(error)).then(() => {
      return reportError(error, {user: event.params.userId});
    });
  });
});

exports.createPayout = functions.database.ref('/tutors/{userId}/payouts/{id}').onWrite(event => {
  const val = event.data.val();
  // Look up the Stripe customer id written in createStripeCustomer
  return admin.database().ref(`/tutors/${event.params.userId}/paymentInfo/${bankAccount}`).once('value').then(snapshot => {
    return snapshot.val();
  }).then(customer => {
    // Create a charge using the pushId as the idempotency key, protecting against double charges
    const amount = val.amount;
    const destination = val.destination;
    const idempotency_key = event.params.id;
    let payout = {
      amount,
      currency,
      destination
    };
    return stripe.payouts.create(payout, {idempotency_key});
  }).then(response => {
      // If the result is successful, write it back to the database
      return event.data.adminRef.set(response);
    }, error => {
      // We want to capture errors and render them in a user-friendly way, while
      // still logging an exception with Stackdriver
      return event.data.adminRef.child('error').set(userFacingMessage(error)).then(() => {
        return reportError(error, {user: event.params.userId});
      });
    }
  );
});

exports.sendMessageNotification = functions.database
.ref('/messageNotifications/{user_id}/{notification_id}').onWrite(event => {
  const user_id = event.params.user_id;
  const notification_id = event.params.notification_id;

  console.log('Sent notification to : ', user_id );

  if(!event.data.val()){
    return console.log('A notification has been deleted from the database : ', notification_id);
  }

  const fromUser = admin.database().ref(`/messageNotifications/${user_id}/${notification_id}`).once('value');
  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('Received notification from : ', from_user_id);

    const userQuery = admin.database().ref(`users/${from_user_id}/firstName`).once('value');
    return userQuery.then(userResult => {

      const firstName = userResult.val();

      const deviceToken = admin.database().ref(`/users/${user_id}/deviceToken`).once('value');

      return deviceToken.then(result => {

        const token_id = result.val();

        const payload = {
          notification: {
            title : "Message",
            body : `${firstName} sent you a message!`,
            icon : "default"
          }
        };

        return admin.messaging().sendToDevice(token_id , payload).then(response => {
          console.log('Notification sent');
        });

      });

    });

  });

});

exports.sendRequestNotification = functions.database
.ref('/requestNotifications/{user_id}/{notification_id}').onWrite(event => {
  const user_id = event.params.user_id;
  const notification_id = event.params.notification_id;

  console.log('Sent request to : ', user_id );

  if(!event.data.val()){
    return console.log('A request has been deleted from the database : ', notification_id);
  }

  const fromUser = admin.database().ref(`/requestNotifications/${user_id}/${notification_id}`).once('value');
  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('Received notification from : ', from_user_id);

    const userQuery = admin.database().ref(`users/${from_user_id}/firstName`).once('value');
    return userQuery.then(userResult => {

      const firstName = userResult.val();

      const deviceToken = admin.database().ref(`/users/${user_id}/deviceToken`).once('value');

      return deviceToken.then(result => {

        const token_id = result.val();

        const payload = {
          notification: {
            title : "Session Request",
            body : `${firstName} sent you a session request!`,
            icon : "default"
          }
        };

        return admin.messaging().sendToDevice(token_id , payload).then(response => {
          console.log('Notification sent');
        });

      });

    });

  });

});


exports.confirmedNotification = functions.database
.ref('/confirmedNotifications/{user_id}/{notification_id}').onWrite(event => {
  const user_id = event.params.user_id;
  const notification_id = event.params.notification_id;

  console.log('Sent request to : ', user_id );

  if(!event.data.val()){
    return console.log('A request has been deleted from the database : ', notification_id);
  }

  const fromUser = admin.database().ref(`/confirmedNotifications/${user_id}/${notification_id}`).once('value');
  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('Received notification from : ', from_user_id);

    const userQuery = admin.database().ref(`users/${from_user_id}/firstName`).once('value');
    return userQuery.then(userResult => {

      const firstName = userResult.val();

      const deviceToken = admin.database().ref(`/users/${user_id}/deviceToken`).once('value');

      return deviceToken.then(result => {

        const token_id = result.val();

        const payload = {
          notification: {
            title : "Request Confirmed",
            body : `${firstName} accepted your session request!`,
            icon : "default"
          }
        };

        return admin.messaging().sendToDevice(token_id , payload).then(response => {
          console.log('Notification sent');
        });

      });

    });

  });

});

exports.cancelledNotification = functions.database
.ref('/cancelledNotifications/{user_id}/{notification_id}').onWrite(event => {
  const user_id = event.params.user_id;
  const notification_id = event.params.notification_id;

  console.log('Sent request to : ', user_id );

  if(!event.data.val()){
    return console.log('A request has been deleted from the database : ', notification_id);
  }

  const fromUser = admin.database().ref(`/cancelledNotifications/${user_id}/${notification_id}`).once('value');
  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('Received notification from : ', from_user_id);

    const userQuery = admin.database().ref(`users/${from_user_id}/firstName`).once('value');
    return userQuery.then(userResult => {

      const firstName = userResult.val();

      const deviceToken = admin.database().ref(`/users/${user_id}/deviceToken`).once('value');

      return deviceToken.then(result => {

        const token_id = result.val();

        const payload = {
          notification: {
            title : "Session Cancelled",
            body : `${firstName} cancelled the session.`,
            icon : "default"
          }
        };

        return admin.messaging().sendToDevice(token_id , payload).then(response => {
          console.log('Notification sent');
        });

      });

    });

  });

});

exports.declinedNotification = functions.database
.ref('/declinedNotifications/{user_id}/{notification_id}').onWrite(event => {
  const user_id = event.params.user_id;
  const notification_id = event.params.notification_id;

  console.log('Sent request to : ', user_id );

  if(!event.data.val()){
    return console.log('A request has been deleted from the database : ', notification_id);
  }

  const fromUser = admin.database().ref(`/declinedNotifications/${user_id}/${notification_id}`).once('value');
  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('Received notification from : ', from_user_id);

    const userQuery = admin.database().ref(`users/${from_user_id}/firstName`).once('value');
    return userQuery.then(userResult => {

      const firstName = userResult.val();

      const deviceToken = admin.database().ref(`/users/${user_id}/deviceToken`).once('value');

      return deviceToken.then(result => {

        const token_id = result.val();

        const payload = {
          notification: {
            title : "Session Cancelled",
            body : `${firstName} declined the session.`,
            icon : "default"
          }
        };

        return admin.messaging().sendToDevice(token_id , payload).then(response => {
          console.log('Notification sent');
        });

      });

    });

  });

});

// To keep on top of errors, we should raise a verbose error report with Stackdriver rather
// than simply relying on console.error. This will calculate users affected + send you email
// alerts, if you've opted into receiving them.
// [START reporterror]
function reportError(err, context = {}) {
  // This is the name of the StackDriver log stream that will receive the log
  // entry. This name can be any valid log stream name, but must contain "err"
  // in order for the error to be picked up by StackDriver Error Reporting.
  const logName = 'errors';
  const log = logging.log(logName);

  // https://cloud.google.com/logging/docs/api/ref_v2beta1/rest/v2beta1/MonitoredResource
  const metadata = {
    resource: {
      type: 'cloud_function',
      labels: { function_name: process.env.FUNCTION_NAME }
    }
  };

  // https://cloud.google.com/error-reporting/reference/rest/v1beta1/ErrorEvent
  const errorEvent = {
    message: err.stack,
    serviceContext: {
      service: process.env.FUNCTION_NAME,
      resourceType: 'cloud_function'
    },
    context: context
  };

  // Write the error log entry
  return new Promise((resolve, reject) => {
    log.write(log.entry(metadata, errorEvent), error => {
      if (error) { reject(error); }
      resolve();
    });
  });
}
// [END reporterror]

// Sanitize the error message for the user
function userFacingMessage(error) {
  return error.type ? error.message : 'Something went wrong.';
}
