document.addEventListener('DOMContentLoaded', function() {
    const listViewBtn = document.getElementById('list-view');
    const gridViewBtn = document.getElementById('grid-view');
    const articlesContainer = document.getElementById('articles-container');

    if (listViewBtn && gridViewBtn) {
        listViewBtn.addEventListener('click', function() {
            articlesContainer.classList.remove('grid-view');
            listViewBtn.classList.add('active');
            gridViewBtn.classList.remove('active');
        });

        gridViewBtn.addEventListener('click', function() {
            articlesContainer.classList.add('grid-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
        });
    }

    const showUploadBtn = document.getElementById('show-upload-form');
    const uploadContainer = document.getElementById('upload-form-container');
    const closeUploadBtn = document.getElementById('close-upload-form');

    if (showUploadBtn) {
        showUploadBtn.addEventListener('click', function() {
            uploadContainer.style.display = 'flex';
        });
    }

    if (closeUploadBtn) {
        closeUploadBtn.addEventListener('click', function() {
            uploadContainer.style.display = 'none';
        });
    }

    if (uploadContainer) {
        uploadContainer.addEventListener('click', function(e) {
            if (e.target === uploadContainer) {
                uploadContainer.style.display = 'none';
            }
        });
    }

    const form = document.getElementById('bibtex-upload-form');
    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();
            const statusEl = document.getElementById('upload-status');
            const formData = new FormData(form);

            statusEl.textContent = 'Загрузка...';
            statusEl.style.color = '#007bff';

            fetch(form.action, {
                method: 'POST',
                body: formData
            }).then(response => {
                if (response.ok) {
                    statusEl.textContent = 'Статьи успешно загружены!';
                    statusEl.style.color = 'green';
                    setTimeout(() => {
                        location.reload();
                    }, 1500);
                } else {
                    statusEl.textContent = 'Ошибка загрузки. Попробуйте ещё раз.';
                    statusEl.style.color = 'red';
                }
            }).catch(error => {
                statusEl.textContent = 'Ошибка сети. Попробуйте ещё раз.';
                statusEl.style.color = 'red';
            });
        });
    }
});
