class SpecialtyPagination {
    constructor() {
        this.searchInput = document.getElementById('searchInput');
        this.specialtyList = document.querySelector('.education-list');
        this.paginationContainer = document.querySelector('.pagination');
        this.currentUrl = this.paginationContainer?.dataset.currentUrl || '';

        this.init();
    }

    init() {
        if (this.searchInput) {
            this.searchInput.addEventListener('input', this.handleSearch.bind(this));
        }

        // Add click handlers for pagination
        if (this.paginationContainer) {
            this.paginationContainer.addEventListener('click', (e) => {
                const link = e.target.closest('.page-link');
                if (link && !link.parentElement.classList.contains('disabled')) {
                    e.preventDefault();
                    const url = new URL(link.href);
                    // Only navigate if we're not searching
                    if (!this.searchInput.value.trim()) {
                        window.location.href = link.href;
                    }
                }
            });
        }
    }

    handleSearch() {
        const searchTerm = this.searchInput.value.toLowerCase().trim();
        const items = this.specialtyList.querySelectorAll('.education-item');

        let visibleCount = 0;

        items.forEach(item => {
            const text = item.textContent.toLowerCase();
            const isVisible = text.includes(searchTerm);
            item.style.display = isVisible ? '' : 'none';
            if (isVisible) visibleCount++;
        });

        // Hide pagination when searching
        if (this.paginationContainer) {
            this.paginationContainer.style.display = searchTerm ? 'none' : '';
        }
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    new SpecialtyPagination();
});