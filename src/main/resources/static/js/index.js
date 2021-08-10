fillTable();

function fillTable() {
    fetch('/upload/list')
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            let container = document.getElementById('table');
            container.classList.add('loading');
            container.innerHTML = "";

            for (let pair of data) {
                container.innerHTML += "    <tr>\n" +
                    "      <th scope=\"row\">" + pair['id'] + "</th>\n" +
                    "      <td>" + pair['key'] + "</td>\n" +
                    "      <td>" + pair['value'] + "</td>\n" +
                    "      <td>" + pair['timeStamp'] + "</td>\n" +
                    "          <td>\n" +
                    "            <button type=\"button\" class=\"btn btn-danger\" id=\"" + pair['key'] + "\" onclick=\"actionDelete(this.id)\"><i class=\"far fa-trash-alt\"></i>Delete</button>" +
                    "          </td>\n" +
                    "    </tr>";
            }

            container.classList.remove('loading');
        });
}

function actionDelete(id) {
    console.log("Delete elem: " + id);
    fetch('/upload/' + id, {
        method: 'DELETE',
    }).then(response => {
        return response.json()
    }).then(data =>
        console.log(data)
    );

    location.reload();
}

function actionLoad() {
    let input = document.createElement('input');
    input.type = 'file';

    input.onchange = e => {
        let file = e.target.files[0];
        let formData = new FormData();

        formData.append("file", file);
        fetch('/upload/load', {method: "POST", body: formData});
        location.reload();
    }

    input.click();
}
