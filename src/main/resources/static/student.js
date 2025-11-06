
window.onload = () => {
    document.getElementById('filterSorter').addEventListener("submit", async (event) => {
        event.preventDefault();

        const sortBy = document.getElementById('sortBy').value;
        const pageNum = document.getElementById('pageNum').value;
        const selectedGroup = document.getElementById('selectedGroup').value;
        
        const studentData = await (await fetch("/fetchedStudent?sortBy=" + sortBy + "&pageNum=" + pageNum + "&selectedGroup=" + selectedGroup)).json();
        const studentList = await studentData.students;
        const pageCount = studentData.pageCount;

        const table = document.getElementById('studentTable');
        const tableBody = table.getElementsByTagName('tbody');
        if(tableBody.length != 0) {
            table.removeChild(tableBody[0]);
        }
        const newTableBody = document.createElement('tbody');
        table.appendChild(newTableBody);
        

        for(let i = 0; i < studentList.length; i++) {
            const student = studentList[i];
            const row = await newTableBody.insertRow(i);

            const img = document.createElement('img');
            img.src =  '/imgs/' + student.imgName;
            img.height = 50;
            
            const link = document.createElement('a');
            link.href = '/student/' + student.id;
            link.innerHTML = "Own Page";
            
            (await row.insertCell(0)).innerHTML = student.id;
            row.insertCell(1).appendChild(img);
            row.insertCell(2).innerHTML = student.firstName;
            row.insertCell(3).innerHTML = student.lastName;
            row.insertCell(4).innerHTML = student.email;
            row.insertCell(5).innerHTML = student.cnp;
            row.insertCell(6).innerHTML = student.groupName;
            row.insertCell(7).appendChild(link);
        }

        const pageSelector = document.getElementById('pageNum');
 
        while(pageSelector.lastElementChild) {
            pageSelector.removeChild(pageSelector.lastElementChild)
        }

        let currentPage = pageNum;

        if(currentPage > pageCount) {
            currentPage = pageCount;
        }

        for(let i = 1; i <= pageCount; i++) {
            const option = document.createElement("option");
            option.text = i;
            if(i == currentPage) {
                option.selected = "selected";
            }
            pageSelector.appendChild(option);
        }

    });
};