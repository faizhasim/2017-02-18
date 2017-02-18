const R = require('ramda');
const AWS = require('aws-sdk');

const ses = new AWS.SES();
// const emailsender = require('./emailsender');

const extractJSONDataFromSNSMessage = R.pipe(
  R.prop('Records'),
  R.head,
  R.prop('Sns'),
  R.prop('Message'),
  JSON.parse
);



module.exports = (event, context, callback) => {

  const jsonMsg = extractJSONDataFromSNSMessage(event);
  const toAddress = jsonMsg.publisher.user.email;
  const params = {
    Destination: {
      ToAddresses: [toAddress]
    },
    Message: {

      Subject: {
        Data: 'test',
        Charset: 'UTF-8'
      },

      Body: {
        Html: {
          Data: JSON.stringify(jsonMsg) + "<br/><br/><b>HELLO!!!</b>",
          Charset: 'UTF-8'
        }
      }
    },
    Source: 'faizhasim@gmail.com',
    ReplyToAddresses: [
      'Mohd Faiz Hasim <faizhasim@gmail.com>'
    ]
  };
  console.log(jsonMsg, params);
  console.log(JSON.stringify(jsonMsg));
  // const sample = {"id":7,"name":"2017-02-18T17:30:41","publishDate":1487410244350,"publisher":{"id":1,"name":"Test Publisher1 "},"uuid":"003d06ae-0aa7-4677-a796-863d17786ea7","category":{"id":1,"name":"immunology"}};

  ses.sendEmail(params, (err, data) => {
    if (err) {
      console.log(err, err.stack);
      // return reject(err);
      callback(err);
    }
    // return resolve(data);
    callback(null, data);
  });

  // callback(null, {ok: true});
};


