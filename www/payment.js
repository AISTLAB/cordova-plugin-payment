// cordova.define("cordova-plugin-payment.payment", function (require, factory) {
//     var exec = require('cordova/exec');

//     exports.exeWft = function (order, success, error) {
//         exec(success, error, "Payment", "exeWft", [order]);
//     };
// });

var payment = {
    exec:function(action,args,succCallback,errorCallback){
        cordova.exec(
            succCallback,
            errorCallback,
            "PaymentPlugin",
            action,
            [args]
        )
    }
}
module.exports=payment;

