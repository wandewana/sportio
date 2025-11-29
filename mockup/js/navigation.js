// Navigation & Screen Management

const screens = [
  { id: 'login', label: 'Login' },
  { id: 'register', label: 'Register' },
  { id: 'home', label: 'Home - Sessions' },
  { id: 'propose', label: 'Propose Session' },
  { id: 'session-wait', label: 'Session Lobby' },
  { id: 'session-detail', label: 'Session Detail' },
  { id: 'venue-suggest', label: 'Venue Suggestions' },
  { id: 'booking', label: 'Booking Slot' },
  { id: 'booking-confirm', label: 'Booking Confirm' },
  { id: 'chat', label: 'Chat' },
  { id: 'profile', label: 'Profile' },
  { id: 'notifications', label: 'Notifications' },
  { id: 'player-join', label: 'Joined Confirmation' },
  { id: 'report', label: 'Report' }
];

// Initialize screen navigation
function initNavigation() {
  const list = document.getElementById('screens-list');
  screens.forEach(s => {
    const btn = document.createElement('div');
    btn.className = 'screen-btn';
    btn.innerHTML = `<div>${s.label}</div><small>preview</small>`;
    btn.onclick = () => go(s.id);
    list.appendChild(btn);
  });
}

// Navigate to a specific screen
function go(id) {
  document.querySelectorAll('.screen').forEach(el => el.classList.remove('active'));
  const target = document.getElementById(id);
  if (target) target.classList.add('active');
  window.scrollTo(0, 0);
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', initNavigation);

