import {points, setGlobalR, checkRange} from './points.js';
import {savePoints, clearPoints} from './storage.js';
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

const clickCheckButton = () => {
    hideError();

    const x = parseInt(document.getElementById('x')?.value);
    const y = toDecimal(document.getElementById('y')?.value);
    const selectedR = document.querySelector('input[name="r"]:checked');
    const r = selectedR ? Number(selectedR.value) : null;

    if (y == null) {
        showError('Координата Y не соответсвует числу');
        return;
    }

    if (r == null) {
        showError('Значение R не соответсвует числу');
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

    const tmp = Date.now();
    const is_range = checkRange(x, y) ? 'Попала' : 'Не попала';
    const point = [x, y, r, is_range, tmp];

    points.push(point);

    savePoints(points);
    createLine(x, y, r);
    addPointToTable(point);

    drawZoomUpdate(zoom);
};

const clickClearButton = () => {
    hideError();

    points.length = 0;

    clearPoints();
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

addToTable();
drawZoomUpdate(zoom);
