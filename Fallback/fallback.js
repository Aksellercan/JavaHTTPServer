const listContent = document.getElementById('scopeListShow');
const mainContent = document.getElementById('buttonDiv');
const scopesArray = []
var redirect_uri = 'http://127.0.0.1:7277/Fallback/fallback.html';
document.getElementById('setClient_id').value = '';
addList()

document.getElementById('redirectForm').addEventListener('submit',function(event) {
    event.preventDefault();
    var client_id = document.getElementById('setClient_id').value;
    var scope = '';
    var state = generateRandomString(16);
    console.log(`State: ${state}`);
    if (scopesArray.length === 0) {
        alert("No Scopes added");
        return;
    }    
    for (let i = 0; i < scopesArray.length; i++) {
        if (i === 0) {
        scope += scopesArray[i];    
        continue;
        }
        scope += " " +scopesArray[i];
    }
    console.log(`DEBUG ${scopesArray.length} ` + (scopesArray.length === 1 ? `Scope: ${scope}` :`Scopes: ${scope}`));
    var url = 'https://accounts.spotify.com/authorize?' +
        new URLSearchParams({
            response_type: 'code',
            client_id: client_id,
            scope: scope,
            redirect_uri: redirect_uri,
            state:state
        })
    // window.location.href = url;
    window.open(url, '_blank').focus();
});

function addList(){
    const addListBtn = document.createElement('button');
        addListBtn.href = "#";
        addListBtn.className = "buttonClassWider";
        addListBtn.id = "submitBtn";
        addListBtn.textContent = "Add Scope";
    addListBtn.addEventListener('click', function(event){
        event.preventDefault();
        var setScope = document.getElementById('setScopes').value;
        if (typeof setScope === "string" && setScope.trim().length === 0) {
            return;
        }
        scopesArray.push(setScope);
        loop();
        document.getElementById('setScopes').value = '';
    });
    mainContent.appendChild(addListBtn)
}

function loop(){
    listContent.innerHTML = '';
    for (let i = 0; i < scopesArray.length; i++) {
        const listView = document.createElement('li');
        listView.className = "jsList";
        listView.textContent = " " + scopesArray[i];
        listContent.appendChild(listView);
    }
}

function generateRandomString(length) {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * characters.length);
        result += characters.charAt(randomIndex);
    }
    return result;
}