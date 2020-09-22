let maxValueLen = [];
let data;

$("div").each( function( key, value ) {
    let newValueLen = value.textContent.split(/\<.*?\>|&amp|&nbsp|\[.*?\]|\{.*?\}|!|/).join("").split(" ").length;

    if(newValueLen > maxValueLen){
        let attrNames = value.getAttributeNames();
        let attrMap = []
        attrNames.forEach(name =>
                attrMap.push({name : value.getAttribute(name)})
                );

        data = {content: value.textContent.split(/\<.*?\>|\\s+|&amp|&nbsp|\[.*?\]|\{.*?\}/).join(""), attributes : attrMap }
        maxValueArr = newValueArr;
    }
});

JSON.stringify(data)

console.log(maxValueArr);