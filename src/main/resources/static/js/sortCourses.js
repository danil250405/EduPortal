document.addEventListener('DOMContentLoaded', function () {
    // Search functionality
    const searchInput = document.getElementById('searchInput');
    const table = document.getElementById('courseTable');
    const tableRows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');

    if (searchInput) {
        searchInput.addEventListener('input', function () {
            const searchTerm = this.value.toLowerCase().trim();

            for (let i = 0; i < tableRows.length; i++) {
                let rowText = tableRows[i].textContent.toLowerCase();
                if (rowText.includes(searchTerm)) {
                    tableRows[i].style.display = "";
                } else {
                    tableRows[i].style.display = "none";
                }
            }
        });
    }

    // Add event listeners for sorting
    const headers = document.querySelectorAll('#courseTable th.sortable');
    headers.forEach(header => {
        // Добавляем иконку для сортировки
        const icon = document.createElement('span');
        icon.classList.add('sort-icon');
        icon.innerHTML = ' ↕️';
        header.appendChild(icon);

        header.addEventListener('click', function() {
            const n = parseInt(this.getAttribute('data-sort'));
            sortTable(n);
        });
    });

    function sortTable(n) {
        const table = document.getElementById("courseTable");
        const thead = table.getElementsByTagName("thead")[0];
        const headers = thead.getElementsByTagName("th");
        let dir = "asc";

        // Toggle sort direction and update sort icons
        for (let i = 0; i < headers.length; i++) {
            if (i === n) {
                if (headers[i].classList.contains("asc")) {
                    dir = "desc";
                    headers[i].classList.remove("asc");
                    headers[i].classList.add("desc");
                    // Обновляем иконку
                    headers[i].querySelector('.sort-icon').innerHTML = ' ↓';
                } else {
                    dir = "asc";
                    headers[i].classList.remove("desc");
                    headers[i].classList.add("asc");
                    // Обновляем иконку
                    headers[i].querySelector('.sort-icon').innerHTML = ' ↑';
                }
            } else {
                headers[i].classList.remove("asc", "desc"); // Remove from other headers
                // Сбрасываем иконку для других заголовков
                if (headers[i].querySelector('.sort-icon')) {
                    headers[i].querySelector('.sort-icon').innerHTML = ' ↕️';
                }
            }
        }

        let rows, switching, i, x, y, shouldSwitch, switchcount = 0;
        switching = true;

        while (switching) {
            switching = false;
            rows = table.rows;
            for (i = 1; i < (rows.length - 1); i++) {
                shouldSwitch = false;

                let x, y;
                const td = rows[i].getElementsByTagName("TD");
                const nextTd = rows[i+1].getElementsByTagName("TD");

                switch (n) {
                    case 0: // Course Name
                        x = td[n].getElementsByTagName("SPAN")[0].innerText.toLowerCase();
                        y = nextTd[n].getElementsByTagName("SPAN")[0].innerText.toLowerCase();
                        break;
                    case 1: // Teacher
                        x = td[n].innerText.toLowerCase();
                        y = nextTd[n].innerText.toLowerCase();
                        break;
                    case 2: // Files Count
                        x = parseInt(td[n].innerText);
                        y = parseInt(nextTd[n].innerText);
                        break;
                    case 3: // Created At (Date and Time)
                        const dateStringX = td[n].innerText;
                        const dateStringY = nextTd[n].innerText;

                        // Parse date strings, handling potential variations in format
                        x = parseDateString(dateStringX);
                        y = parseDateString(dateStringY);
                        break;
                    default:
                        console.error("Unknown column index for sorting:", n);
                        continue;
                }

                if (dir === "asc") {
                    if (x > y) {
                        shouldSwitch = true;
                        break;
                    }
                } else {
                    if (x < y) {
                        shouldSwitch = true;
                        break;
                    }
                }
            }

            if (shouldSwitch) {
                rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                switching = true;
                switchcount++;
            }
        }
    }

    // Helper function to parse date strings, handling different formats
    function parseDateString(dateString) {
        // Attempt to parse using different formats
        let parsedDate = new Date(dateString); // Try default Date parsing first

        // If default parsing fails, try parsing with a specific format
        if (isNaN(parsedDate.getTime())) {
            // Example: dd-MM-yyyy HH:mm:ss or dd-MM-yyyy HH:mm
            const parts = dateString.split(/[- :]/);
            if (parts.length >= 5) {
                const day = parseInt(parts[0], 10);
                const month = parseInt(parts[1], 10) - 1; // Month is 0-indexed
                const year = parseInt(parts[2], 10);
                const hour = parseInt(parts[3], 10);
                const minute = parseInt(parts[4], 10);
                const second = parts.length > 5 ? parseInt(parts[5], 10) : 0; // Optional seconds
                parsedDate = new Date(year, month, day, hour, minute, second);
            }
        }

        // If parsing still fails, return an invalid date
        if (isNaN(parsedDate.getTime())) {
            console.warn("Could not parse date:", dateString);
            return new Date(NaN); // Return an invalid date
        }

        return parsedDate;
    }
});