import { loadPoints } from './storage.js';

export const points = loadPoints();
export let global_r = 1;

export const setGlobalR = (value) => {
  global_r = value;
};

export const checkRange = (x, y) => {
  if (x >= -global_r && x <= 0 && y >= 0 && y <= global_r / 2) {
    return true;
  }
  else if (x ** 2 + y ** 2 <= (global_r / 2) ** 2 && x <= 0 && y <= 0) {
    return true;
  }
  else if (y >= x - global_r && y <= 0 && x >= 0 && x <= global_r) {
    return true;
  }
  return false;
};
