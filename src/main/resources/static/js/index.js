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
                "      <th scope=\"row\">#</th>\n" +
                "      <td>" + pair['key'] + "</td>\n" +
                "      <td>" + pair['value'] + "</td>\n" +
                "      <td>" + pair['timeStamp'] + "</td>\n" +
                "    </tr>";
        }

        container.classList.remove('loading');
    });
