import {points, setGlobalR, fetchValidate} from './points.js';
import {loadPoints, clearPoints} from './storage.js';
import {toDecimal, showError, hideError} from './validation.js';
import {addPointToTable, clearTable, addToTable} from './table.js';

import {
    canvas,
    zoom,
    drawZoomUpdate,
    zoomOperation,
    selectedFromMove,
    moveMouse,
    unselectedFromMove,
    createPoint,
} from './canvas.js';

const buttonClear = document.getElementById('clear-button');
const rCheckboxes = document.querySelectorAll('input[name="r"]');

rCheckboxes.forEach((checkbox) => {
    checkbox.addEventListener('change', () => {
        if (!checkbox.checked) {
            checkbox.checked = true;
            return;
        }

        rCheckboxes.forEach((other) => {
            if (other !== checkbox) {
                other.checked = false;
            }
        });

        setGlobalR(Number(checkbox.value));
        drawZoomUpdate(zoom);
    });
});

const clickCheckButton = async () => {
    hideError();

    const x = parseInt(document.getElementById('x')?.value);
    const y = toDecimal(document.getElementById('y')?.value);
    const selectedR = document.querySelector('input[name="r"]:checked');
    const r = selectedR ? Number(selectedR.value) : null;

    if (y == null) {
        showError('Координата Y не соответствует числу');
        return;
    }

    if (r == null) {
        showError('Значение R не соответствует числу');
        return;
    }

    if (y.lt(-5) || y.gt(5)) {
        showError('Y должен находиться в диапазоне от -5 до 5');
        return;
    }

    if (r < 1 || r > 5) {
        showError('R должен находиться в диапазоне от 1 до 5');
        return;
    }

    setGlobalR(r);

    let point;
    try {
        point = await fetchValidate(x, y.toString(), r);
    } catch (err) {
        showError(`Точка не добавлена: ${err.message}`);
        return;
    }

    points.push(point);

    addPointToTable(point);

    drawZoomUpdate(zoom);
};

const clickClearButton = async () => {
    hideError();

    try {
        await clearPoints();
    } catch (err) {
        showError(`Сервер недоступен, точки не удалены: ${err.message}`);
        return;
    }

    points.length = 0;
    clearTable();
    drawZoomUpdate(zoom);
};

canvas.addEventListener('wheel', zoomOperation);
canvas.addEventListener('mousedown', selectedFromMove);
canvas.addEventListener('mousemove', moveMouse);
canvas.addEventListener('dblclick', createPoint);
window.addEventListener('mouseup', unselectedFromMove);

buttonClear.addEventListener('click', clickClearButton);

document.getElementById('point-form').addEventListener('submit', (event) => {
    event.preventDefault();
    clickCheckButton();
});

const loadFromServer = async () => {
    try {
        points.push(...await loadPoints());
    } catch (err) {
        showError(`Сервер недоступен, точки не загружены: ${err.message}`);
    }

    addToTable();
    drawZoomUpdate(zoom);
};

loadFromServer();