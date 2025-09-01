let animationStep = 0;
let isAnimating = false;

// Create floating particles
function createParticles() {
    const particlesContainer = document.getElementById('particles');
    for (let i = 0; i < 50; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.top = Math.random() * 100 + '%';
        particle.style.animationDelay = Math.random() * 6 + 's';
        particlesContainer.appendChild(particle);
    }
}

function updateStatus(message) {
    document.getElementById('status').textContent = message;
}

function showResponse(elementId, message) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.style.opacity = '1';
    element.style.transform = 'translateY(-30px)';

    setTimeout(() => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(-10px)';
    }, 2000);
}

function addGlowEffect(elementId) {
    const element = document.getElementById(elementId);
    element.classList.add('glow-effect');

    setTimeout(() => {
        element.classList.remove('glow-effect');
    }, 2000);
}

function showJWTToken() {
    const token = document.getElementById('jwtToken');
    token.style.opacity = '1';
    token.style.transform = 'scale(1)';

    setTimeout(() => {
        token.style.opacity = '0';
        token.style.transform = 'scale(0.8)';
    }, 3000);
}

function animateRequest(from, to, callback) {
    const fromElement = document.getElementById(from);
    const toElement = document.getElementById(to);

    const fromRect = fromElement.getBoundingClientRect();
    const toRect = toElement.getBoundingClientRect();

    const dot = document.createElement('div');
    dot.className = 'request-dot';
    dot.style.left = (fromRect.left + fromRect.width / 2) + 'px';
    dot.style.top = (fromRect.top + fromRect.height / 2) + 'px';
    document.body.appendChild(dot);

    // Animate dot movement
    dot.style.opacity = '1';
    dot.style.transition = 'all 1.5s cubic-bezier(0.25, 0.46, 0.45, 0.94)';

    setTimeout(() => {
        dot.style.left = (toRect.left + toRect.width / 2) + 'px';
        dot.style.top = (toRect.top + toRect.height / 2) + 'px';
    }, 100);

    setTimeout(() => {
        dot.style.opacity = '0';
        setTimeout(() => {
            document.body.removeChild(dot);
            if (callback) callback();
        }, 500);
    }, 1600);
}

async function startAnimation() {
    if (isAnimating) return;
    isAnimating = true;

    // Step 1: User request to Gateway
    updateStatus("👤 User making request to API Gateway...");
    addGlowEffect('user');
    showResponse('userResponse', 'POST /api/login');

    animateRequest('user', 'gateway', () => {
        // Step 2: Gateway to Auth Service
        setTimeout(() => {
            updateStatus("🌐 Gateway routing to Auth Service...");
            addGlowEffect('gateway');
            showResponse('gatewayResponse', 'Forwarding to auth...');

            animateRequest('gateway', 'auth', () => {
                // Step 3: Auth Service to Database
                setTimeout(() => {
                    updateStatus("🔐 Auth Service validating credentials...");
                    addGlowEffect('auth');
                    showResponse('authResponse', 'Validating user...');

                    animateRequest('auth', 'database', () => {
                        // Step 4: Database response
                        setTimeout(() => {
                            updateStatus("🗄️ Database validating user...");
                            addGlowEffect('database');
                            showResponse('dbResponse', 'User found ✓');

                            animateRequest('database', 'auth', () => {
                                // Step 5: Auth generates JWT
                                setTimeout(() => {
                                    updateStatus("🔐 Generating JWT token...");
                                    addGlowEffect('auth');
                                    showResponse('authResponse', 'JWT generated!');
                                    showJWTToken();

                                    animateRequest('auth', 'gateway', () => {
                                        // Step 6: Gateway to User
                                        setTimeout(() => {
                                            updateStatus("🌐 Returning JWT to user...");
                                            addGlowEffect('gateway');
                                            showResponse('gatewayResponse', 'JWT token ready');

                                            animateRequest('gateway', 'user', () => {
                                                // Step 7: Complete
                                                setTimeout(() => {
                                                    updateStatus("✅ Authentication complete! User can access protected resources.");
                                                    addGlowEffect('user');
                                                    showResponse('userResponse', 'Authenticated! 🎉');
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

function showRegisterFlow() {
    if (isAnimating) return;
    isAnimating = true;

    updateStatus("📝 User registration flow...");
    addGlowEffect('user');
    showResponse('userResponse', 'POST /auth/register');

    animateRequest('user', 'gateway', () => {
        setTimeout(() => {
            updateStatus("🌐 Gateway routing to Auth Service for registration...");
            addGlowEffect('gateway');
            showResponse('gatewayResponse', 'New user registration');

            animateRequest('gateway', 'auth', () => {
                setTimeout(() => {
                    updateStatus("🔐 Creating new user account...");
                    addGlowEffect('auth');
                    showResponse('authResponse', 'Creating account...');

                    animateRequest('auth', 'database', () => {
                        setTimeout(() => {
                            updateStatus("🗄️ Saving user to database...");
                            addGlowEffect('database');
                            showResponse('dbResponse', 'User created ✓');

                            animateRequest('database', 'auth', () => {
                                setTimeout(() => {
                                    updateStatus("🔐 Registration complete, generating JWT...");
                                    addGlowEffect('auth');
                                    showResponse('authResponse', 'Welcome! JWT created');
                                    showJWTToken();

                                    animateRequest('auth', 'gateway', () => {
                                        setTimeout(() => {
                                            animateRequest('gateway', 'user', () => {
                                                setTimeout(() => {
                                                    updateStatus("🎉 Registration successful! User is now authenticated.");
                                                    addGlowEffect('user');
                                                    showResponse('userResponse', 'Account created! 🎊');
                                                    isAnimating = false;
                                                }, 500);
                                            });
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
}

function resetAnimation() {
    updateStatus("Ready to authenticate...");
    isAnimating = false;

    // Remove any existing dots
    const dots = document.querySelectorAll('.request-dot');
    dots.forEach(dot => {
        if (dot.parentNode) {
            dot.parentNode.removeChild(dot);
        }
    });

    // Reset all glow effects
    document.querySelectorAll('.glow-effect').forEach(el => {
        el.classList.remove('glow-effect');
    });

    // Hide all response texts
    document.querySelectorAll('.response-text').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(-10px)';
    });

    // Hide JWT token
    document.getElementById('jwtToken').style.opacity = '0';
    document.getElementById('jwtToken').style.transform = 'scale(0.8)';
}


document.addEventListener('DOMContentLoaded', () => {
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');

    hamburger.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });
});

// Initialize
createParticles();