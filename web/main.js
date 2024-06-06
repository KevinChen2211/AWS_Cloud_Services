// Get Session storage 
const userUrl = sessionStorage.getItem("userUrl");
const subscriptionUrl = sessionStorage.getItem("subscriptionUrl");
const musicUrl = sessionStorage.getItem("musicUrl");

const user_name = sessionStorage.getItem("user_name");
const email = sessionStorage.getItem("email");

// Initializing varibles
const subscriptionArea = document.getElementById("subscriptionList");

// Run
document.getElementById("welcomeUser").textContent = "Welcome, " + user_name + "!";
getMusicDetails(getUserSubscription());

// Functions
function getMusicDetails(musicArray) {
    const subscriptionArea = document.getElementById("subscriptionList");
    subscriptionArea.innerHTML = "";
    for (index in musicArray) {
        console.log("getting details of" + musicArray[index])
        $.ajax({
            url: musicUrl,
            dataType: 'json',
            type: 'post',
            async: false,
            contentType: 'application/json',
            data: JSON.stringify({
                "web_url": musicArray[index],
                "type": "getDetails",
                "title": "",
                "year": "",
                "artist": ""
            }),
            processData: false,
            success: function (data, textStatus, jQxhr) {
                if (data.body) {
                    createMusicSubscriptionCard(data.body.title, data.body.year, data.body.artist, musicArray[index]);
                } else {
                    console.log("Something went wrong")
                }
            },
            error: function (jqXhr, textStatus, errorThrown) {
                console.log("There has been an error" + errorThrown);
            }
        });
    }
}

function createMusicSubscriptionCard(title, year, artist, url) {
    const card = document.createElement("div");
    card.classList.add("music-card");

    const titleDiv = document.createElement("h3");
    titleDiv.textContent = title;
    card.appendChild(titleDiv);

    const yearDiv = document.createElement("p");
    yearDiv.textContent = `Year: ${year}`;
    card.appendChild(yearDiv);

    const artistDiv = document.createElement("p");
    artistDiv.textContent = `Artist: ${artist}`;
    card.appendChild(artistDiv);

    const removeButton = document.createElement("button");
    removeButton.textContent = "Remove";
    removeButton.classList.add("remove-button");


    removeButton.addEventListener("click", function () {
        removeSubscriptionCard(url)
        subscriptionArea.removeChild(card);
    });

    card.appendChild(removeButton);

    subscriptionArea.appendChild(card);
}

function removeSubscriptionCard(url) {
    $.ajax({
        url: subscriptionUrl,
        dataType: 'json',
        type: 'post',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "email": email,
            "type": "remove",
            "web_url": url
        }),
        processData: false,
        success: function (data, textStatus, jQxhr) {
            console.log("removed Card");
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log("There has been an error" + errorThrown);
        }
    });
}

function getUserSubscription() {
    console.log("Gettting User subscribed Music");
    gottenUserSubscriptions = "";
    $.ajax({
        url: subscriptionUrl,
        dataType: 'json',
        type: 'post',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "email": email,
            "type": "get",
            "web_url": ""
        }),
        processData: false,
        success: function (data, textStatus, jQxhr) {
            if (data.body.subscriptions.length == 0) {
                document.getElementById("subscriptionEmpty").textContent = "No Subscriptions";
            } else {
                document.getElementById("subscriptionEmpty").textContent = "";
                gottenUserSubscriptions = data.body.subscriptions;
            }
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log("There has been an error" + errorThrown);
        }
    });
    return gottenUserSubscriptions;
}

function queryMusic(form) {
    console.log("Querying music")
    document.getElementById("resultsFound").textContent = "";
    $.ajax({
        url: musicUrl,
        dataType: 'json',
        type: 'post',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "web_url": "",
            "type": "search",
            "title": form.title.value,
            "year": form.year.value,
            "artist": form.artist.value
        }),
        processData: false,
        success: function (data, textStatus, jQxhr) {
            if (data.body) {
                if (data.body.search.length != 0) {
                    const queryList = document.getElementById("queryList");
                    queryList.innerHTML = "";

                    for (const musicData of data.body.search) {
                        createMusicCard(musicData.title, musicData.year, musicData.artist, musicData.web_url, musicData.image_url);
                    }
                } else {
                    document.getElementById("resultsFound").textContent = "No result is retrieved. Please query again";
                    const queryList = document.getElementById("queryList");
                    queryList.innerHTML = "";
                }
            } else {
                console.log("Something went wrong")
                const queryList = document.getElementById("queryList");
                queryList.innerHTML = "";
            }
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log("There has been an error" + errorThrown);
        }
    });
}

function createMusicCard(title, year, artist, web_url, image_url) {

    const card = document.createElement("div");
    card.classList.add("music-card");

    const imageDiv = document.createElement("div");
    imageDiv.classList.add("music-card-image");
    const image = document.createElement("img");
    image.src = "https://s3780646-assignment1-task2.s3.amazonaws.com/" + getFileName(image_url);

    imageDiv.appendChild(image);
    card.appendChild(imageDiv);

    const detailsDiv = document.createElement("div");
    detailsDiv.classList.add("music-card-details");

    const titleDiv = document.createElement("h3");
    titleDiv.textContent = title;
    detailsDiv.appendChild(titleDiv);

    const yearDiv = document.createElement("p");
    yearDiv.textContent = `Year: ${year}`;
    detailsDiv.appendChild(yearDiv);

    const artistDiv = document.createElement("p");
    artistDiv.textContent = `Artist: ${artist}`;
    detailsDiv.appendChild(artistDiv);

    const button = document.createElement("button");
    button.textContent = "Subscribe";
    button.classList.add("subscribe-button");

    button.onclick = function () {
        addSubscriptionToUser(web_url)
    };

    detailsDiv.appendChild(button);
    card.appendChild(detailsDiv);

    const queryList = document.getElementById("queryList");
    queryList.appendChild(card);
}

function getFileName(image_url) {
    return filename = image_url.split("/").pop();
}

function addSubscriptionToUser(url) {
    $.ajax({
        url: subscriptionUrl,
        dataType: 'json',
        type: 'post',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "email": email,
            "type": "update",
            "web_url": url
        }),
        processData: false,
        success: function (data, textStatus, jQxhr) {
            console.log("added to subscription");
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log("There has been an error" + errorThrown);
        }
    });
    getMusicDetails(getUserSubscription());
}