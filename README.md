# Spring boot + H2 application to mock simple payment between accounts

All accounts data is available via: GET: localhost:8080/api/account/searchAll

Payment can be done via POST, sending JSON to "localhost:8080/api/payment/process":

Curl example:
```
curl --location --request POST 'http://localhost:8080/api/payment/process' \
--header 'Content-Type: application/json' \
--data-raw '{
"senderAccountId": 1,
"receiverAccountId": "2",
"amount": "1.00"
}
'
```

If result is OK, then success PaymentDto is retrieved:

{
"senderAccountId": "1",
"receiverAccountId": "2",
"amount": "100.00",
"timestamp": "2022-11-06T16:21:47.959926800"
}

All processed payments data is available via: GET: localhost:8080/api/payment/searchAllProcessedPayments
