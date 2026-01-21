// API Base URL (same-origin)
const API_BASE = '/api';

// Global State
let currentPage = 0;
let currentCategory = null;
let currentSearch = '';
let token = localStorage.getItem('token');
let username = localStorage.getItem('username');
let categories = [];
let cart = null;

// Local fallback images (served from /images after copying into static/images)
const FALLBACK_IMAGES = [
    '/images/tiger-75-1681798245-1700472656.webp',
    '/images/Swaraj-855-FE1732352123_zh8kn4U3B.webp',
    '/images/Kubota-MU4501-4WD1735902101_U232v14Z4.webp',
    '/images/KhedutDiscHarrow7x7_0_1468216497.webp',
    '/images/LemkenMouldBoardPlough2MBPlough_0_1460008779.webp',
    '/images/john-deere-technology-solution-banner.webp',
    '/images/ShaktimanRotaryTillerU-seriesU84_0_1459771599.webp',
    '/images/agritechnica-highlight.png',
    '/images/logo512.png',
    '/images/blog-11-detail-page-blog-thumbnails.webp'
];

function pickFallbackImage(key) {
    if (!FALLBACK_IMAGES.length) return 'https://via.placeholder.com/400';
    const idx = Math.abs((key || 0)) % FALLBACK_IMAGES.length;
    return FALLBACK_IMAGES[idx];
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadCategories();
    loadProducts();
    if (token) {
        loadCart();
    }

    // Debounced search
    const searchEl = document.getElementById('searchInput');
    if (searchEl) {
        const debounced = debounce(() => searchProducts(), 300);
        searchEl.addEventListener('input', debounced);
        searchEl.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') searchProducts();
        });
    }
});

function debounce(fn, waitMs) {
    let t;
    return (...args) => {
        clearTimeout(t);
        t = setTimeout(() => fn(...args), waitMs);
    };
}

// Auth Functions
function checkAuth() {
    if (token && username) {
        document.getElementById('authSection').style.display = 'none';
        document.getElementById('userSection').style.display = 'flex';
        document.getElementById('usernameDisplay').textContent = username;
    }
}

function showLogin() {
    document.getElementById('loginModal').style.display = 'block';
}

function closeLoginModal() {
    document.getElementById('loginModal').style.display = 'none';
    document.getElementById('loginError').textContent = '';
}

function showRegister() {
    document.getElementById('registerModal').style.display = 'block';
}

function closeRegisterModal() {
    document.getElementById('registerModal').style.display = 'none';
    document.getElementById('registerError').textContent = '';
}

function login() {
    const loginUsername = document.getElementById('loginUsername').value;
    const loginPassword = document.getElementById('loginPassword').value;

    fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: loginUsername, password: loginPassword })
    })
    .then(res => res.json())
    .then(data => {
        if (data.token) {
            token = data.token;
            localStorage.setItem('token', token);
            localStorage.setItem('username', data.username);
            username = data.username;
            closeLoginModal();
            checkAuth();
            loadCart();
        } else {
            document.getElementById('loginError').textContent = 'Invalid credentials';
        }
    })
    .catch(err => {
        document.getElementById('loginError').textContent = 'Login failed';
    });
}

function register() {
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;

    fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password })
    })
    .then(res => res.json())
    .then(data => {
        if (data.message) {
            closeRegisterModal();
            showLogin();
        } else {
            document.getElementById('registerError').textContent = 'Registration failed';
        }
    })
    .catch(err => {
        document.getElementById('registerError').textContent = 'Registration failed';
    });
}

function logout() {
    token = null;
    username = null;
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    checkAuth();
    cart = null;
    updateCartCount();
}

// Categories
function loadCategories() {
    fetch(`${API_BASE}/categories`)
        .then(res => res.json())
        .then(data => {
            categories = data;
            const categoryList = document.getElementById('categoryList');
            categoryList.innerHTML = data.map(cat => 
                `<button class="category-btn" onclick="filterByCategory(${cat.id})">${cat.name}</button>`
            ).join('');
        });
}

function filterByCategory(categoryId) {
    currentCategory = categoryId;
    currentPage = 0;
    document.querySelectorAll('.category-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    loadProducts();
}

// Currency formatter for INR
const formatINR = (value) => {
    const num = Number(value || 0);
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(num);
};

// Products
function loadProducts() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('productsGrid').innerHTML = '';

    let url = `${API_BASE}/products?page=${currentPage}&size=12`;
    if (currentCategory) url += `&categoryId=${currentCategory}`;
    if (currentSearch) url += `&search=${encodeURIComponent(currentSearch)}`;

    fetch(url)
        .then(res => res.json())
        .then(data => {
            document.getElementById('loading').style.display = 'none';
            displayProducts(data.content || data);
            if (data.totalPages !== undefined) {
                displayPagination(data);
            }
        })
        .catch(err => {
            document.getElementById('loading').textContent = 'Error loading products (check console)';
            console.error('loadProducts error', err);
        });
}

function displayProducts(products) {
    const grid = document.getElementById('productsGrid');
    if (!products || products.length === 0) {
        grid.innerHTML = '<div style="color:white;text-align:center;padding:2rem;">No products found</div>';
        return;
    }
    grid.innerHTML = products.map(product => `
        <div class="product-card" onclick="showProductDetail(${product.id})">
            <img src="${product.imageUrl || pickFallbackImage(product.id)}"
                 alt="${product.name}"
                 class="product-image"
                 onerror="this.src='${pickFallbackImage(product.id)}'; this.onerror=null;">
            <div class="product-info">
                <div class="product-name">${product.name}</div>
                <div class="product-price">${formatINR(product.price)}</div>
                <div class="product-stock">Stock: ${product.stock}</div>
                ${product.sourceUrl ? `
                    <a href="${product.sourceUrl}" target="_blank" rel="noopener noreferrer"
                       style="display:inline-block;margin-top:.4rem;color:#9fffd1;text-decoration:underline;"
                       onclick="event.stopPropagation();">
                        View on Source
                    </a>
                ` : ''}
                <div class="product-actions">
                    <button class="btn-primary" onclick="event.stopPropagation(); addToCart(${product.id})">
                        Add to Cart
                    </button>
                    <button class="btn-secondary" onclick="event.stopPropagation(); buyNow(${product.id})">
                        Buy Now
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

function displayPagination(pageData) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    for (let i = 0; i < pageData.totalPages; i++) {
        const btn = document.createElement('button');
        btn.textContent = i + 1;
        btn.className = i === currentPage ? 'active' : '';
        btn.onclick = () => {
            currentPage = i;
            loadProducts();
        };
        pagination.appendChild(btn);
    }
}

function searchProducts() {
    currentSearch = document.getElementById('searchInput').value;
    currentPage = 0;
    loadProducts();
}

// Product Detail
function showProductDetail(productId) {
    fetch(`${API_BASE}/products/${productId}`)
        .then(res => res.json())
        .then(product => {
            const modal = document.getElementById('productModal');
            const detail = document.getElementById('productDetail');
            const imgSrc = product.imageUrl || pickFallbackImage(product.id);
            detail.innerHTML = `
                <div class="product-detail">
                    <img src="${imgSrc}"
                         alt="${product.name}"
                         class="product-detail-image"
                         onerror="this.src='${pickFallbackImage(product.id)}'; this.onerror=null;">
                    <div class="product-detail-info">
                        <h2>${product.name}</h2>
                        <div class="product-detail-price">${formatINR(product.price)}</div>
                        <p><strong>Stock:</strong> ${product.stock}</p>
                        <p><strong>Brand:</strong> ${product.brand || 'N/A'}</p>
                        <p><strong>Origin:</strong> ${product.origin || 'N/A'}</p>
                        <p><strong>Unit:</strong> ${product.unit || 'N/A'}</p>
                        ${product.sourceUrl ? `
                            <p><strong>Source:</strong>
                                <a href="${product.sourceUrl}" target="_blank" rel="noopener noreferrer">
                                    ${product.sourceUrl}
                                </a>
                            </p>
                        ` : ''}
                        <div class="product-detail-specs">
                            <strong>Description:</strong><br>
                            ${product.description || 'No description available'}
                        </div>
                        ${product.specifications ? `
                            <div class="product-detail-specs">
                                <strong>Specifications:</strong><br>
                                ${product.specifications}
                            </div>
                        ` : ''}
                        <div class="product-actions" style="margin-top: 1rem;">
                            <button class="btn-primary" onclick="addToCart(${product.id})">Add to Cart</button>
                            <button class="btn-secondary" onclick="buyNow(${product.id})">Buy Now</button>
                        </div>
                    </div>
                </div>
            `;
            modal.style.display = 'block';
        });
}

function closeModal() {
    document.getElementById('productModal').style.display = 'none';
}

// Cart
function loadCart() {
    if (!token) return;
    
    fetch(`${API_BASE}/cart`, {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(data => {
        cart = data;
        updateCartCount();
    })
    .catch(err => console.error('Cart load error:', err));
}

function addToCart(productId) {
    if (!token) {
        alert('Please login first');
        showLogin();
        return;
    }

    const quantity = prompt('Enter quantity:', '1');
    if (!quantity || quantity <= 0) return;

    fetch(`${API_BASE}/cart/add?productId=${productId}&quantity=${quantity}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(async res => {
        if (!res.ok) {
            const msg = await res.text();
            throw new Error(msg || 'Failed to add to cart');
        }
        return res.json();
    })
    .then(() => {
        loadCart();
        alert('Added to cart!');
    })
    .catch(err => alert(err.message || 'Failed to add to cart'));
}

function showCart() {
    if (!token) {
        alert('Please login first');
        showLogin();
        return;
    }
    
    loadCart();
    const modal = document.getElementById('cartModal');
    modal.style.display = 'block';
    displayCartItems();
}

function displayCartItems() {
    if (!cart || !cart.cartItems || cart.cartItems.length === 0) {
        document.getElementById('cartItems').innerHTML = '<p>Your cart is empty</p>';
        document.getElementById('cartTotal').textContent = formatINR(0);
        return;
    }

    const itemsHtml = cart.cartItems.map(item => {
        const unitPrice = parseFloat(item.price) / item.quantity;
        return `
        <div class="cart-item">
            <div class="cart-item-info">
                <strong>${item.product.name}</strong><br>
                <span>${formatINR(unitPrice)} x ${item.quantity} = ${formatINR(item.price)}</span>
            </div>
            <div class="cart-item-actions">
                <button onclick="updateCartItem(${item.id}, ${item.quantity - 1})">-</button>
                <span>${item.quantity}</span>
                <button onclick="updateCartItem(${item.id}, ${item.quantity + 1})">+</button>
                <button onclick="removeCartItem(${item.id})">Remove</button>
            </div>
        </div>
    `;
    }).join('');

    document.getElementById('cartItems').innerHTML = itemsHtml;
    
    const total = cart.cartItems.reduce((sum, item) => sum + parseFloat(item.price), 0);
    document.getElementById('cartTotal').textContent = formatINR(total);
}

function updateCartItem(itemId, quantity) {
    if (quantity <= 0) {
        removeCartItem(itemId);
        return;
    }

    fetch(`${API_BASE}/cart/item/${itemId}?quantity=${quantity}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => {
        if (res.ok) {
            loadCart();
            displayCartItems();
        } else {
            alert('Failed to update cart item');
        }
    })
    .catch(err => {
        alert('Failed to update cart item');
    });
}

function removeCartItem(itemId) {
    fetch(`${API_BASE}/cart/item/${itemId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(() => {
        loadCart();
        displayCartItems();
    });
}

function closeCartModal() {
    document.getElementById('cartModal').style.display = 'none';
}

function updateCartCount() {
    const count = cart && cart.cartItems ? cart.cartItems.length : 0;
    document.getElementById('cartCount').textContent = count;
}

function placeOrder() {
    if (!cart || !cart.cartItems || cart.cartItems.length === 0) {
        alert('Cart is empty');
        return;
    }

    const shippingAddress = prompt('Enter shipping address (required):', 'House No, Street, City, State, Pincode');
    if (!shippingAddress || !shippingAddress.trim()) {
        alert('Shipping address is required');
        return;
    }

    fetch(`${API_BASE}/orders`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ shippingAddress })
    })
    .then(res => res.json())
    .then(data => {
        alert('Order placed successfully!');
        closeCartModal();
        loadCart();
    })
    .catch(err => alert('Failed to place order'));
}

function buyNow(productId) {
    if (!token) {
        alert('Please login first');
        showLogin();
        return;
    }

    const quantity = prompt('Enter quantity:', '1');
    if (!quantity || quantity <= 0) return;

    const shippingAddress = prompt('Enter shipping address (required):', 'House No, Street, City, State, Pincode');
    if (!shippingAddress || !shippingAddress.trim()) {
        alert('Shipping address is required');
        return;
    }

    fetch(`${API_BASE}/orders/buy-now?productId=${productId}&quantity=${quantity}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ shippingAddress })
    })
    .then(res => res.json())
    .then(data => {
        alert('Order placed successfully!');
    })
    .catch(err => alert('Failed to place order'));
}

// Orders
function showOrders() {
    if (!token) {
        alert('Please login first');
        showLogin();
        return;
    }

    fetch(`${API_BASE}/orders`, {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(orders => {
        const modal = document.getElementById('ordersModal');
        const list = document.getElementById('ordersList');
        
        if (orders.length === 0) {
            list.innerHTML = '<p>No orders found</p>';
        } else {
            list.innerHTML = orders.map(order => `
                <div style="border: 1px solid #e0e0e0; padding: 1rem; margin: 1rem 0; border-radius: 8px;">
                    <strong>Order #${order.id}</strong><br>
                    <span>Date: ${new Date(order.orderDate).toLocaleDateString()}</span><br>
                    <span>Status: ${order.status}</span><br>
                    <span>Total: $${order.totalAmount.toFixed(2)}</span>
                </div>
            `).join('');
        }
        
        modal.style.display = 'block';
    });
}

function closeOrdersModal() {
    document.getElementById('ordersModal').style.display = 'none';
}

// Close modals when clicking outside
window.onclick = function(event) {
    const modals = ['productModal', 'cartModal', 'loginModal', 'registerModal', 'ordersModal'];
    modals.forEach(modalId => {
        const modal = document.getElementById(modalId);
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}
