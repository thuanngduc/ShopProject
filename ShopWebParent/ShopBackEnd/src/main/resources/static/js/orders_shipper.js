var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;
var iconNames = {
    'PICKED': 'fa-people-carry',
    'SHIPPING': 'fa-shipping-fast',
    'DELIVERED': 'fa-box-open',
    'RETURNED': 'fa-undo'
};
$(document).ready(function(){
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmModal");
    yesButton = $("#yesButton");
    noButton = $("#noButton");

    $(".linkUpdateStatus").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        showUpdateConfirmModal(link);
    });

    addEvenHandlerForYesButton();
});

function addEvenHandlerForYesButton() {
    yesButton.click(function (e) {
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
    });
}
function sendRequestToUpdateOrderStatus(button) {
    requestURL = button.attr("href");
    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr)
        {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
    }).done(function (response){
        showMessageModal("Order updated successfully");
        updateStatusIconColor(response.orderId, response.status);
    }).fail(function (err) {
        showMessageModal("Error updating order status");
    });
}
function updateStatusIconColor(orderId, status) {
    link = $("#link" + status + orderId);
    link.replaceWith("<i class='fas " + iconNames[status] + " fa-2x icon-green'  ></i>")


}
function showUpdateConfirmModal(link) {
    orderId = link.attr("orderId");
    status = link.attr("status");
    yesButton.attr("href", link.attr("href"));


    confirmText.text("Are you sure you want to update status of the order ID #"+orderId + " to " + status + "?");
    confirmModalDialog.modal();

}
function showMessageModal(message) {
    noButton.text("Close");
    yesButton.hide();
    confirmText.text(message);
}
