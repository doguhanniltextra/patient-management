let isAnimating = false;

function updateStatus(message) {
    const statusEl = document.getElementById('status');
    if (statusEl) statusEl.textContent = message;
}

function showResponse(elementId, message) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    // Clear previous classes
    element.className = 'node-response';
    element.textContent = message;
    
    // Position the response relative to the target node
    const targetNode = elementId.replace('Response', '');
    const nodeEl = document.getElementById(targetNode);
    if (nodeEl) {
        const rect = nodeEl.getBoundingClientRect();
        const mainRect = document.querySelector('.main-content').getBoundingClientRect();
        element.style.left = (rect.left - mainRect.left + rect.width / 2) + 'px';
        element.style.top = (rect.top - mainRect.top - 40) + 'px';
        element.style.display = 'block';
    }

    setTimeout(() => {
        element.classList.add('show');
    }, 10);

    setTimeout(() => {
        element.classList.remove('show');
        setTimeout(() => { element.style.display = 'none'; }, 300);
    }, 2500);
}

function addGlowEffect(elementId) {
    const element = document.getElementById(elementId);
    if (!element) return;
    element.classList.add('node-active', 'pulse-node');

    setTimeout(() => {
        element.classList.remove('node-active', 'pulse-node');
    }, 2500);
}

function animateRequest(fromId, toId, callback) {
    const fromElement = document.getElementById(fromId);
    const toElement = document.getElementById(toId);
    if (!fromElement || !toElement) return;

    const fromRect = fromElement.querySelector('.node-icon').getBoundingClientRect();
    const toRect = toElement.querySelector('.node-icon').getBoundingClientRect();

    const dot = document.createElement('div');
    dot.style.position = 'fixed';
    dot.style.width = '8px';
    dot.style.height = '8px';
    dot.style.background = '#2563eb';
    dot.style.borderRadius = '50%';
    dot.style.boxShadow = '0 0 10px rgba(37, 99, 235, 0.5)';
    dot.style.zIndex = '10000';
    dot.style.pointerEvents = 'none';
    dot.style.left = (fromRect.left + fromRect.width / 2 - 4) + 'px';
    dot.style.top = (fromRect.top + fromRect.height / 2 - 4) + 'px';
    dot.style.opacity = '0';
    dot.style.transition = 'all 1s cubic-bezier(0.4, 0, 0.2, 1)';
    
    document.body.appendChild(dot);

    setTimeout(() => {
        dot.style.opacity = '1';
        dot.style.left = (toRect.left + toRect.width / 2 - 4) + 'px';
        dot.style.top = (toRect.top + toRect.height / 2 - 4) + 'px';
    }, 50);

    setTimeout(() => {
        dot.style.opacity = '0';
        setTimeout(() => {
            if (dot.parentNode) dot.parentNode.removeChild(dot);
            if (callback) callback();
        }, 300);
    }, 1050);
}

async function startAnimation() {
    if (isAnimating) return;
    isAnimating = true;

    // Step 1: User request to Gateway
    updateStatus("Auth Event: Initializing login handshake...");
    addGlowEffect('user');
    showResponse('userResponse', 'POST /api/v1/auth/login');

    animateRequest('user', 'gateway', () => {
        // Step 2: Gateway to Auth Service
        setTimeout(() => {
            updateStatus("Gateway: Validating route and filtering headers...");
            addGlowEffect('gateway');
            showResponse('gatewayResponse', 'Routing to Auth Service');

            animateRequest('gateway', 'auth', () => {
                // Step 3: Auth Service to Database
                setTimeout(() => {
                    updateStatus("Auth Svc: Performing credential lookup...");
                    addGlowEffect('auth');
                    showResponse('authResponse', 'SQL: SELECT * FROM users');

                    animateRequest('auth', 'database', () => {
                        // Step 4: Database response
                        setTimeout(() => {
                            updateStatus("Storage: Record matched and verified.");
                            addGlowEffect('database');
                            showResponse('dbResponse', 'Status: ACCESS_GRANTED');

                            animateRequest('database', 'auth', () => {
                                // Step 5: Auth generates JWT
                                setTimeout(() => {
                                    updateStatus("Auth Svc: Generating RS256 JWT Token...");
                                    addGlowEffect('auth');
                                    showResponse('authResponse', 'JWT Provisioned');

                                    animateRequest('auth', 'gateway', () => {
                                        // Step 6: Gateway to User
                                        setTimeout(() => {
                                            updateStatus("Gateway: Relay authorized payload to client.");
                                            addGlowEffect('gateway');
                                            showResponse('gatewayResponse', '200 OK (Auth Token)');

                                            animateRequest('gateway', 'user', () => {
                                                // Step 7: Complete
                                                setTimeout(() => {
                                                    updateStatus("Handshake Complete: Secure session established.");
                                                    addGlowEffect('user');
                                                    showResponse('userResponse', 'Session Active ✓');
                                                    isAnimating = false;
                                                }, 500);
                                            });
                                        }, 800);
                                    });
                                }, 800);
                            });
                        }, 800);
                    });
                }, 800);
            });
        }, 800);
    });
}

function resetAnimation() {
    updateStatus("System Ready");
    isAnimating = false;
    const dots = document.querySelectorAll('[style*="background: #2563eb"]');
    dots.forEach(dot => dot.remove());
}

document.addEventListener('DOMContentLoaded', () => {
    // Sidebar active state handling
    const navItems = document.querySelectorAll('.nav-item');
    window.addEventListener('scroll', () => {
        let current = '';
        const sections = document.querySelectorAll('section');
        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            if (pageYOffset >= sectionTop - 150) {
                current = section.getAttribute('id');
            }
        });

        navItems.forEach(item => {
            item.classList.remove('active');
            if (item.getAttribute('href').substring(1) === current) {
                item.style.background = '#f1f5f9';
                item.style.color = '#2563eb';
            } else {
                item.style.background = '';
                item.style.color = '';
            }
        });
    });

    const hamburger = document.querySelector('.hamburger');
    const sidebar = document.querySelector('.sidebar');
    if (hamburger && sidebar) {
        hamburger.addEventListener('click', () => {
            sidebar.style.display = sidebar.style.display === 'block' ? 'none' : 'block';
        });
    }
});