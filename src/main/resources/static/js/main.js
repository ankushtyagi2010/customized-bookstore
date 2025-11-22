/**
 * Custom Book Store - Main JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize components
    initDropdowns();
    initAlertDismiss();
    initImagePreview();
    initFormValidation();
});

/**
 * Initialize dropdown menus
 */
function initDropdowns() {
    const dropdowns = document.querySelectorAll('.dropdown');

    dropdowns.forEach(dropdown => {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        const menu = dropdown.querySelector('.dropdown-menu');

        if (toggle && menu) {
            toggle.addEventListener('click', function(e) {
                e.stopPropagation();
                menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
            });
        }
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', function() {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.style.display = 'none';
        });
    });
}

/**
 * Auto-dismiss alerts after 5 seconds
 */
function initAlertDismiss() {
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.3s';
            setTimeout(() => {
                alert.remove();
            }, 300);
        }, 5000);
    });
}

/**
 * Image preview for file uploads
 */
function initImagePreview() {
    const fileInputs = document.querySelectorAll('input[type="file"][accept*="image"]');

    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    // Find or create preview element
                    let preview = input.parentElement.querySelector('.image-preview');
                    if (!preview) {
                        preview = document.createElement('div');
                        preview.className = 'image-preview';
                        preview.innerHTML = '<img src="" alt="Preview">';
                        input.parentElement.appendChild(preview);
                    }
                    preview.querySelector('img').src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    });
}

/**
 * Basic form validation
 */
function initFormValidation() {
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            let isValid = true;

            // Check required fields
            const requiredFields = form.querySelectorAll('[required]');
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.classList.add('is-invalid');
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            // Check email fields
            const emailFields = form.querySelectorAll('input[type="email"]');
            emailFields.forEach(field => {
                if (field.value && !isValidEmail(field.value)) {
                    isValid = false;
                    field.classList.add('is-invalid');
                }
            });

            // Check password confirmation
            const password = form.querySelector('#password');
            const confirmPassword = form.querySelector('#confirmPassword');
            if (password && confirmPassword) {
                if (password.value !== confirmPassword.value) {
                    isValid = false;
                    confirmPassword.classList.add('is-invalid');
                }
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    });
}

/**
 * Email validation helper
 */
function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

/**
 * Quantity selector for cart
 */
function updateQuantity(cartItemId, change) {
    const quantityInput = document.querySelector(`#quantity-${cartItemId}`);
    if (quantityInput) {
        let newValue = parseInt(quantityInput.value) + change;
        if (newValue < 1) newValue = 1;
        if (newValue > 10) newValue = 10;
        quantityInput.value = newValue;
    }
}

/**
 * Confirmation dialog for delete actions
 */
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item?');
}

/**
 * Toast notification
 */
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // Trigger animation
    setTimeout(() => toast.classList.add('show'), 10);

    // Remove after 3 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

/**
 * Character image selection handler
 */
function selectCharacterImage(slotId, imageId, imageUrl) {
    // Update hidden input
    const input = document.querySelector(`input[name="characterSlots[${slotId}].selectedImageId"]`);
    if (input) {
        input.value = imageId;
    }

    // Update visual selection
    const container = document.querySelector(`#character-slot-${slotId}`);
    if (container) {
        container.querySelectorAll('.character-option').forEach(opt => {
            opt.classList.remove('selected');
        });
        const selected = container.querySelector(`[data-image-id="${imageId}"]`);
        if (selected) {
            selected.classList.add('selected');
        }
    }
}

/**
 * Cover option handler
 */
function selectCoverOption(option) {
    const uploadSection = document.getElementById('cover-upload-section');
    const defaultPreview = document.getElementById('default-cover-preview');
    const customPreview = document.getElementById('custom-cover-preview');

    if (option === 'custom') {
        if (uploadSection) uploadSection.classList.remove('hidden');
    } else {
        if (uploadSection) uploadSection.classList.add('hidden');
    }
}

/**
 * Live preview update
 */
function updateLivePreview() {
    const titleInput = document.getElementById('customTitle');
    const titlePreview = document.getElementById('preview-title');

    if (titleInput && titlePreview) {
        titlePreview.textContent = titleInput.value || titleInput.placeholder;
    }

    const dedicationInput = document.getElementById('dedication');
    const dedicationPreview = document.getElementById('preview-dedication');

    if (dedicationInput && dedicationPreview) {
        dedicationPreview.textContent = dedicationInput.value || 'No dedication';
    }
}

/**
 * Smooth scroll to element
 */
function scrollToElement(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

/**
 * Format currency
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

/**
 * Debounce function for performance
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Add to cart animation
 */
function animateAddToCart(button) {
    const originalText = button.textContent;
    button.textContent = 'Added!';
    button.classList.add('btn-success');
    button.disabled = true;

    setTimeout(() => {
        button.textContent = originalText;
        button.classList.remove('btn-success');
        button.disabled = false;
    }, 1500);
}
