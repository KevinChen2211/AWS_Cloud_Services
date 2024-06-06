// Initialize varibles
/**
 * NOTE:
 * THESE WERE TEMPORARY AND WILL NOT WORK
 */

const userUrl =
  "https://o5ajn6au18.execute-api.us-east-1.amazonaws.com/Production/userFunction";
sessionStorage.setItem("userUrl", userUrl);

const subscriptionUrl =
  "https://4g12fujxxl.execute-api.us-east-1.amazonaws.com/Production/userSubscriptions";
sessionStorage.setItem("subscriptionUrl", subscriptionUrl);

const musicUrl =
  "https://vlrbjrr5vj.execute-api.us-east-1.amazonaws.com/Production/musicDetails";

sessionStorage.setItem("musicUrl", musicUrl);
const submitButton = document.getElementById("submitButton");
// button clicks
submitButton.addEventListener("click", function (event) {
  event.preventDefault(); // Prevent default form submission
  document.getElementById("error-message").style.display = "none";
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  if (verifyEmail(email)) {
    if (email.length !== 0 && password.length !== 0) {
      console.log("checking for correct details");
      verifyEmailPass(email, password);
    } else {
      displayError("Do not leave values blank");
    }
  }
});

// Functions
function verifyEmailPass(email, pass) {
  $.ajax({
    url: userUrl,
    dataType: "json",
    type: "post",
    async: false,
    contentType: "application/json",
    data: JSON.stringify({
      email: email,
      type: "get",
      user_name: "test",
      password: "test",
    }),
    processData: false,
    success: function (data, textStatus, jQxhr) {
      console.log(data);
      if (data.body && typeof data.body.password !== "undefined") {
        if (data.body.password == pass) {
          storeUserData(email, data.body.user_name);
          window.location.href = "main.html";
        } else {
          displayError("Invalid email or password.");
        }
      } else {
        displayError("Invalid email or password.");
      }
    },
    error: function (jqXhr, textStatus, errorThrown) {
      console.log("There has been an error" + errorThrown);
      displayError("Something went wrong!");
    },
  });
}

function storeUserData(email, user_name) {
  sessionStorage.setItem("email", email);
  sessionStorage.setItem("user_name", user_name);
}

function displayError(text) {
  document.getElementById("error-message").textContent = text;
  document.getElementById("error-message").style.display = "block";
}

function verifyEmail(email) {
  const emailRegex =
    /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  if (emailRegex.test(email)) {
    return true;
  }
  displayError("Please enter a valid email address.");
  return false;
}
