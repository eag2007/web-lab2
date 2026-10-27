export const points = [];
export let global_r = 1;

export const setGlobalR = (value) => {
  global_r = value;
};

export const fetchCheck = async (x, y, r) => {
  const params = new URLSearchParams({
    x: x.toString(),
    y: y.toString(),
    global_r: r.toString(),
  });

  const response = await fetch(`http://localhost:8888/api/check?${params}`);

  if (!response.ok) {
    throw new Error(`Статус ${response.status}`);
  }

  return await response.json();
};

export const fetchValidate = async (x, y, r) => {
  const params = new URLSearchParams({
    x: x.toString(),
    y: y.toString(),
    r: r.toString(),
  });

  const response = await fetch(`http://localhost:8888/api/validate?${params}`);

  if (!response.ok) {
    throw new Error(`Статус ${response.status}`);
  }

  return await response.json();
};
