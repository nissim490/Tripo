let selectedValue;

function sendToJava(htmlRowDataElement) {
    javaConnector.captureHtml(htmlRowDataElement);
}

let jsConnector = {
    showResult: function (result) {}
}

function getJsConnector() {
    return jsConnector;
}

window.addEventListener('keypress', function (e){
    sendToJava(selectedValue);
});

Object.keys(window).forEach(key => {
let div = document.createElement('div');

    if (/^onmouseover/.test(key)) {
        window.addEventListener(key.slice(2), event => {

            let attrNames = event.fromElement.getAttributeNames();
            let attrMap = []
            attrNames.forEach(name =>
                    attrMap.push({name : event.fromElement.getAttribute(name)})
                    );

            let data = {content: event.fromElement.innerHTML.split(/\<.*?\>|\\s+|&amp|&nbsp|\[.*?\]|\{.*?\}/).join("") , attributes : attrMap }

            selectedValue = JSON.stringify(data)

        div.className = 'anotherClass';
        div.style.position = 'absolute';
        div.style.content = '';
        div.style.height = `${event.fromElement.offsetHeight +'px'}`;
        div.style.width = `${event.fromElement.offsetWidth +'px'}`;
        div.style.top = `${event.fromElement.offsetTop + 'px'}`;
        div.style.left = `${event.fromElement.offsetLeft + 'px'}`;
        div.style.background = '#05f';
        div.style.opacity = '0.25';

        event.fromElement.appendChild(div);

        });

        if (/^onmouseout/.test(key)) {
        window.removeEventListener("mouseover", RespondMouseOver);
                window.removeChild(div);
        }
    }
});

