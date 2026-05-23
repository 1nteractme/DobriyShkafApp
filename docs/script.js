const tabs = Array.from(document.querySelectorAll('[role="tab"]'));
const panels = Array.from(document.querySelectorAll('[role="tabpanel"]'));
const githubCommitsUrl = 'https://api.github.com/repos/1nteractme/DobriyShkafApp/commits?per_page=8';
const updatesList = document.querySelector('#updates-list');
const updatesStatus = document.querySelector('#updates-status');
const refreshUpdatesButton = document.querySelector('#refresh-updates');
let updatesLoaded = false;

function activateTab(tab, updateHash = true) {
    const tabName = tab.dataset.tab;

    tabs.forEach((item) => {
        item.setAttribute('aria-selected', String(item === tab));
        item.tabIndex = item === tab ? 0 : -1;
    });

    panels.forEach((panel) => {
        panel.classList.toggle('active', panel.id === `panel-${tabName}`);
    });

    if (tabName === 'updates' && !updatesLoaded)
        loadUpdates();

    if (updateHash)
        history.replaceState(null, '', `#${tabName}`);
}

tabs.forEach((tab, index) => {
    tab.addEventListener('click', () => activateTab(tab));
    tab.addEventListener('keydown', (event) => {
        const nextIndex = event.key === 'ArrowRight' ? (index + 1) % tabs.length : event.key === 'ArrowLeft' ? (index - 1 + tabs.length) % tabs.length : null;

        if (nextIndex === null) return;

        event.preventDefault();
        tabs[nextIndex].focus();
        activateTab(tabs[nextIndex]);
    });
});

const initialTab = tabs.find((tab) => `#${tab.dataset.tab}` === window.location.hash) || tabs[0];
activateTab(initialTab, false);

refreshUpdatesButton?.addEventListener('click', () => loadUpdates(true));

async function loadUpdates(force = false) {
    if (!updatesList || !updatesStatus) return;
    if (updatesLoaded && !force) return;

    updatesStatus.textContent = 'Загрузка последних коммитов...';
    updatesList.innerHTML = '';
    if (refreshUpdatesButton) refreshUpdatesButton.disabled = true;

    try {
        const response = await fetch(githubCommitsUrl, {
            headers: { Accept: 'application/vnd.github+json' }
        });

        if (!response.ok)
            throw new Error(`GitHub API вернул статус ${response.status}`);

        const commits = await response.json();
        updatesLoaded = true;
        renderUpdates(commits);
    } catch (error) {
        updatesStatus.textContent = 'Не удалось загрузить обновления с GitHub.';
        updatesList.innerHTML = `<li class="update-item">
            <p class="update-message">Проверьте подключение к интернету или откройте коммиты напрямую на GitHub.</p>
            <a class="update-link" href="https://github.com/1nteractme/DobriyShkafApp/commits/main" target="_blank" rel="noreferrer">Открыть историю коммитов</a>
        </li>`;
    } finally {
        if (refreshUpdatesButton) refreshUpdatesButton.disabled = false;
    }
}

function renderUpdates(commits) {
    if (!Array.isArray(commits) || commits.length === 0) {
        updatesStatus.textContent = 'Коммиты пока не найдены.';
        return;
    }

    updatesStatus.textContent = `Показано последних коммитов: ${commits.length}`;
    updatesList.innerHTML = commits.map((item) => {
        const commit = item.commit || {};
        const author = commit.author || {};
        const title = firstLine(commit.message || 'Без описания');
        const date = formatDate(author.date);
        const name = author.name || item.author?.login || 'Неизвестный автор';
        const sha = (item.sha || '').slice(0, 7);
        const url = item.html_url || `https://github.com/1nteractme/DobriyShkafApp/commit/${item.sha}`;

        return `<li class="update-item">
            <div class="update-title">
                <p class="update-message">${escapeHtml(title)}</p>
                <span class="update-sha">${escapeHtml(sha)}</span>
            </div>
            <p class="update-meta">${escapeHtml(date)} · ${escapeHtml(name)}</p>
            <a class="update-link" href="${escapeHtml(url)}" target="_blank" rel="noreferrer">Открыть commit</a>
        </li>`;
    }).join('');
}

function firstLine(value) {
    return value.split('\n').find((line) => line.trim()).trim();
}

function formatDate(value) {
    if (!value) return 'Дата неизвестна';

    return new Intl.DateTimeFormat('ru-RU', {
        day: '2-digit',
        month: 'long',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(new Date(value));
}

function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, (char) => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    }[char]));
}