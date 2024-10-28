class Pagination {
    constructor(options) {
        this.searchInput = document.getElementById(options.searchInputId);
        this.itemsContainer = document.querySelector(options.itemsContainerClass);
        this.items = this.itemsContainer.querySelectorAll(options.itemClass);
        this.itemsPerPage = options.itemsPerPage || 10;
        this.currentPage = 0;

        this.init();
    }

    init() {
        // Инициализация поиска
        if (this.searchInput) {
            this.searchInput.addEventListener('input', () => {
                this.handleSearch();
            });
        }

        // Инициализация пагинации
        document.querySelectorAll('.pagination .page-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const href = link.getAttribute('href');
                if (href) {
                    const pageNum = new URLSearchParams(href.split('?')[1]).get('page');
                    if (pageNum !== null) {
                        this.currentPage = parseInt(pageNum);
                        this.updateDisplay();
                    }
                }
            });
        });

        // Начальное отображение
        this.updateDisplay();
    }

    handleSearch() {
        const searchTerm = this.searchInput.value.toLowerCase().trim();

        // Фильтрация элементов
        this.items.forEach(item => {
            const text = item.textContent.toLowerCase();
            item.dataset.visible = text.includes(searchTerm) ? 'true' : 'false';
        });

        // Сброс на первую страницу при поиске
        this.currentPage = 0;
        this.updateDisplay();
        this.updatePaginationVisibility();
    }

    updateDisplay() {
        const visibleItems = Array.from(this.items).filter(
            item => item.dataset.visible !== 'false'
        );

        visibleItems.forEach((item, index) => {
            const startIndex = this.currentPage * this.itemsPerPage;
            const endIndex = startIndex + this.itemsPerPage;

            if (index >= startIndex && index < endIndex) {
                item.style.display = '';
            } else {
                item.style.display = 'none';
            }
        });

        this.updatePaginationButtons();
    }

    updatePaginationButtons() {
        const visibleItemsCount = Array.from(this.items).filter(
            item => item.dataset.visible !== 'false'
        ).length;

        const totalPages = Math.ceil(visibleItemsCount / this.itemsPerPage);

        // Обновление состояния кнопок
        document.querySelectorAll('.pagination .page-item').forEach(pageItem => {
            const link = pageItem.querySelector('.page-link');
            if (!link) return;

            if (link.textContent === 'Previous') {
                pageItem.classList.toggle('disabled', this.currentPage === 0);
            } else if (link.textContent === 'Next') {
                pageItem.classList.toggle('disabled', this.currentPage >= totalPages - 1);
            } else {
                const pageNum = parseInt(link.textContent) - 1;
                if (!isNaN(pageNum)) {
                    pageItem.classList.toggle('active', pageNum === this.currentPage);
                }
            }
        });
    }

    updatePaginationVisibility() {
        const visibleItemsCount = Array.from(this.items).filter(
            item => item.dataset.visible !== 'false'
        ).length;

        const paginationElement = document.querySelector('.pagination');
        if (paginationElement) {
            paginationElement.style.display = visibleItemsCount > this.itemsPerPage ? '' : 'none';
        }
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    new Pagination({
        searchInputId: 'searchInput',
        itemsContainerClass: '.education-list',
        itemClass: '.education-item',
        itemsPerPage: 10
    });
});