document.addEventListener('DOMContentLoaded', function() {
    const likeBtn = document.querySelector('.like-btn');
    const commentForm = document.getElementById('add-comment-form');
    const pdfUploadBtn = document.getElementById('show-pdf-upload');
    const pdfUploadModal = document.getElementById('pdf-upload-modal');

    document.querySelectorAll('.close').forEach(closeBtn => {
        closeBtn.addEventListener('click', function() {
            this.closest('.modal').classList.remove('show');
        });
    });

    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('show');
            }
        });
    });

    if (pdfUploadBtn && pdfUploadModal) {
        pdfUploadBtn.addEventListener('click', () => {
            pdfUploadModal.classList.add('show');
        });
    }

    if (likeBtn) {
        likeBtn.addEventListener('click', function() {
            const articleId = this.getAttribute('data-article-id');

            fetch('/api/likes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ articleId: articleId })
            })
                .then(response => response.json())
                .then(data => {
                    likeBtn.classList.toggle('liked', data.liked);
                    document.querySelector('.like-btn .count').textContent = data.likeCount;
                })
                .catch(error => console.error('Error:', error));
        });
    }

    if (commentForm) {
        commentForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const articleId = this.querySelector('input[name="articleId"]').value;
            const text = this.querySelector('textarea[name="text"]').value;

            fetch('/api/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    articleId: articleId,
                    text: text
                })
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    }
});