document.addEventListener('DOMContentLoaded', function() {
    const showUploadFormBtn = document.getElementById('show-upload-form');
    const closeUploadFormBtn = document.getElementById('close-upload-form');
    const uploadFormContainer = document.getElementById('upload-form-container');
    const bibtexUploadForm = document.getElementById('bibtex-upload-form');
    const subscribeBtn = document.querySelector('.subscribe-btn');
    const showSubscribersBtn = document.getElementById('show-subscribers');
    const showSubscriptionsBtn = document.getElementById('show-subscriptions');
    const usersModal = document.getElementById('users-modal');
    const modalTitle = document.getElementById('modal-title');
    const usersList = document.getElementById('users-list');
    const sortSelector = document.getElementById('sort-selector');

    if (sortSelector) {
        sortSelector.addEventListener('change', function() {
            const userId = window.location.search.split('id=')[1].split('&')[0];
            window.location.href = `/user?id=${userId}&sort=${this.value}`;
        });
    }

    if (showSubscribersBtn) {
        showSubscribersBtn.addEventListener('click', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const userId = urlParams.get('id');
            loadUsers(`/user/${userId}/subscribers`, 'Подписчики');
        });
    }

    if (showSubscriptionsBtn) {
        showSubscriptionsBtn.addEventListener('click', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const userId = urlParams.get('id');
            loadUsers(`/user/${userId}/subscriptions`, 'Подписки');
        });
    }

    function loadUsers(url, title) {
        fetch(url)
            .then(r => {
                if (!r.ok) throw new Error(`HTTP error! status: ${r.status}`);
                return r.json();
            })
            .then(users => {
                modalTitle.textContent = title;
                if (!users || users.length === 0) {
                    usersList.innerHTML = '<p>Нет пользователей</p>';
                } else {
                    usersList.innerHTML = users.map(user => `
                    <div class="user-item">
                        <a href="/user?id=${user.user.id}">
                            ${user.user.firstName} ${user.user.lastName} (@${user.user.username})
                        </a>
                    </div>
                `).join('');
                }
                usersModal.classList.add('show');
            })
            .catch(error => {
                console.error('Error loading users:', error);
                usersList.innerHTML = '<p>Ошибка при загрузке</p>';
            });
    }

    // Универсальное закрытие всех модалей по крестику
    document.querySelectorAll('.close').forEach(closeBtn => {
        closeBtn.addEventListener('click', function() {
            this.closest('.modal').classList.remove('show');
        });
    });

    // Закрытие по клику на фон
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('show');
            }
        });
    });

    // Открытие окна добавления статей
    if (showUploadFormBtn) {
        showUploadFormBtn.addEventListener('click', function() {
            uploadFormContainer.classList.add('show');
        });
    }

    // Закрытие окна добавления статей по кнопке
    if (closeUploadFormBtn) {
        closeUploadFormBtn.addEventListener('click', function() {
            uploadFormContainer.classList.remove('show');
        });
    }

    if (bibtexUploadForm) {
        bibtexUploadForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);

            fetch('/import/bibtex', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        });
    }

    if (subscribeBtn) {
        subscribeBtn.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');

            fetch(`/api/subscribe/${userId}`, {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    }

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
});