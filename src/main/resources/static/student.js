async function refresh_students() {
    const sortBy = document.getElementById('sortBy').value;
    const pageNum = document.getElementById('pageNum').value;
    const selectedGroup = document.getElementById('selectedGroup').value;

    const request = await fetch("/api/student?sortBy=" + sortBy + "&pageNum=" + pageNum + "&selectedGroup=" + selectedGroup)
    
    const studentData = await request.json();
    const studentList = await studentData.students;
    const pageCount = studentData.pageCount;
    const groups = studentData.groups;

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

    const groupSelector = document.getElementById('selectedGroup');

    while(groupSelector.lastElementChild) {
        groupSelector.removeChild(groupSelector.lastElementChild)
    }
    let option = document.createElement("option");
    option.text = "All";
    option.value = "";
    groupSelector.appendChild(option);

    for(const group of groups) {
        const option = document.createElement("option");
        option.text = group.name;
        option.value = group.id;
        if(group.id == selectedGroup) {
            option.selected = "selected";
        }
        groupSelector.appendChild(option);
    }

    const groupAlterSelector = document.getElementById("groupIdAlter");
    while(groupAlterSelector.lastElementChild) {
        groupAlterSelector.removeChild(groupAlterSelector.lastElementChild)
    }
    option = document.createElement("option");
    option.text = "";
    option.value = "";
    groupAlterSelector.appendChild(option);
    for(const group of groups) {
        const option = document.createElement("option");
        option.text = group.name;
        option.value = group.id;
        groupAlterSelector.appendChild(option);
    }

}

async function addStudent() {
    const form = document.getElementById('studentAddForm');
    const data = new FormData(form);

    const csrf_token = document.querySelector("meta[name='_csrf']").getAttribute("content");

    await fetch("/api/student", {
        headers: {
            "X-CSRF-TOKEN": csrf_token
        },
        method: "POST",
        credentials: "same-origin",
        body: data
    }).then(resp => {
        if(resp.status != 200) {
            alert("Failed to add Student!");
        }
    });

    form.reset();

}

async function deleteStudent() {
    const form = document.getElementById('studentDeleteForm');
    const data = new FormData(form);

    const csrf_token = document.querySelector("meta[name='_csrf']").getAttribute("content");

    await fetch("/api/student", {
        headers: {
            "X-CSRF-TOKEN": csrf_token
        },
        method: "DELETE",
        credentials: "same-origin",
        body: data
    }).then(resp => {
        if(resp.status != 200) {
            alert("Failed to delete Student!");
        }
    });

    form.reset();

}

async function updateStudent() {
    const form = document.getElementById('studentAlterForm');
    const data = new FormData(form);

    const csrf_token = document.querySelector("meta[name='_csrf']").getAttribute("content");

    await fetch("/api/student", {
        headers: {
            "X-CSRF-TOKEN": csrf_token
        },
        method: "PUT",
        credentials: "same-origin",
        body: data
    }).then(resp => {
        if(resp.status != 200) {
            alert("Failed to Alter Student!");
        }
    });

    form.reset();

}

window.onload = () => {
    document.getElementById('filterSorter').addEventListener("submit", async (event) => {
        event.preventDefault();

        await refresh_students();
    });
    
    if(document.getElementById('studentAddForm')) {
        document.getElementById('studentAddForm').addEventListener("submit", async (event) => {
            event.preventDefault();

            await addStudent();
            await refresh_students();
        });
    }
    
    if(document.getElementById('studentDeleteForm')) {
        document.getElementById('studentDeleteForm').addEventListener("submit", async (event) => {
            event.preventDefault();

            await deleteStudent();
            await refresh_students();
        });
    }

    if(document.getElementById('studentAlterForm')) {
        document.getElementById('studentAlterForm').addEventListener("submit", async (event) => {
            event.preventDefault();

            await updateStudent();
            await refresh_students();
        });
    }

    refresh_students();


};



