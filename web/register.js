
// Initialize varibles
const userUrl = sessionStorage.getItem("userUrl");

const createAccountButton = document.getElementById('createAccountButton');

// button clicks 
createAccountButton.addEventListener('click', function (event) {
    event.preventDefault();
    document.getElementById("error-message").style.display = "none";
    const email = document.getElementById('email').value;
    const user_name = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    if (verifyEmailPass(email)) {
        if (email.length !== 0 && password.length !== 0 && user_name.length !== 0) {
            createUser(email, user_name, password);
        } else {
            displayError("Do not leave values blank");
        }
    }
});


function createUser(email, user_name, pass) {
    $.ajax({
        url: userUrl,
        dataType: 'json',
        type: 'post',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "email": email,
            "type": "update",
            "user_name": user_name,
            "password": pass
        }),
        processData: false,
        success: function (data, textStatus, jQxhr) {
            console.log(data)
            if (data.body && typeof data.body.user_name !== 'undefined') {
                window.location.href = "index.html";
            } else {
                displayError("The email already exists");
            }
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log("There has been an error" + errorThrown);
            displayError("Something went wrong!");
        }
    });
}


function displayError(text) {
    document.getElementById("error-message").textContent = text;
    document.getElementById("error-message").style.display = "block";
}

function verifyEmailPass(email) {
    const emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    if (emailRegex.test(email)) {
        return true;
    }
    displayError("Please enter a valid email address.");
    return false;
}