let pdfDoc = null;
let currentPageNum = 1;
let totalPages = 0;
let pdfBase64Data = null;

document.addEventListener('DOMContentLoaded', function() {
    const likeBtn = document.querySelector('.like-btn');
    const commentForm = document.getElementById('add-comment-form');
    const pdfUploadBtn = document.getElementById('show-pdf-upload');
    const pdfUploadModal = document.getElementById('pdf-upload-modal');
    const pdfViewerContainer = document.getElementById('pdfViewerContainer');

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

    if (pdfViewerContainer) {
        loadPdfViewer();
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

async function loadPdfViewer() {
    const articleId = new URLSearchParams(window.location.search).get('id');

    try {
        const response = await fetch(`/api/articles/${articleId}/pdf-data`);
        const data = await response.json();

        if (data.pdfBase64) {
            pdfBase64Data = data.pdfBase64;
            await loadPdfFromBase64(data.pdfBase64);
        }
    } catch (error) {
        console.error('Ошибка при загрузке PDF:', error);
    }
}

async function loadPdfFromBase64(base64Data) {
    try {
        const binaryString = atob(base64Data);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }

        pdfDoc = await pdfjsLib.getDocument({ data: bytes }).promise;
        totalPages = pdfDoc.numPages;

        document.getElementById('totalPages').textContent = totalPages;
        document.getElementById('pageSlider').max = totalPages;

        await renderPage(1);
    } catch (error) {
        console.error('Ошибка при загрузке PDF:', error);
    }
}

async function renderPage(pageNum) {
    if (!pdfDoc || pageNum < 1 || pageNum > totalPages) return;

    currentPageNum = pageNum;
    document.getElementById('currentPage').textContent = pageNum;
    document.getElementById('pageSlider').value = pageNum;

    const page = await pdfDoc.getPage(pageNum);
    const scale = 1.5;
    const viewport = page.getViewport({ scale });

    const canvas = document.getElementById('pdfCanvas');
    const context = canvas.getContext('2d');

    canvas.width = viewport.width;
    canvas.height = viewport.height;

    const renderContext = {
        canvasContext: context,
        viewport: viewport
    };

    await page.render(renderContext).promise;
}

function nextPage() {
    if (currentPageNum < totalPages) {
        renderPage(currentPageNum + 1);
    }
}

function previousPage() {
    if (currentPageNum > 1) {
        renderPage(currentPageNum - 1);
    }
}

function goToPage(pageNum) {
    renderPage(parseInt(pageNum));
}

function downloadPdf() {
    if (pdfBase64Data) {
        const link = document.createElement('a');
        link.href = 'data:application/pdf;base64,' + pdfBase64Data;
        link.download = document.querySelector('.article-title')?.textContent || 'article.pdf';
        link.click();
    }
}
