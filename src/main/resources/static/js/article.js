document.addEventListener('DOMContentLoaded', function() {
    const likeBtn = document.querySelector('.like-btn');

    if (likeBtn) {
        likeBtn.addEventListener('click', function() {
            const articleId = this.getAttribute('data-article-id');
            const button = this;

            fetch('/api/likes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ articleId: articleId })
            })
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    const countSpan = button.querySelector('.count');
                    countSpan.textContent = data.likeCount;

                    const textSpan = button.querySelector('.text');
                    if (data.liked) {
                        button.classList.add('liked');
                        textSpan.textContent = 'Нравится';
                    } else {
                        button.classList.remove('liked');
                        textSpan.textContent = 'Нравится';
                    }
                })
                .catch(function(error) {
                    console.error('Error:', error);
                });
        });
    }



    const commentForm = document.getElementById('add-comment-form');

    if (commentForm) {
        commentForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const data = {
                articleId: formData.get('articleId'),
                text: formData.get('text')
            };

            fetch('/api/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Ошибка при добавлении комментария');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Ошибка при добавлении комментария');
                });
        });
    }
});