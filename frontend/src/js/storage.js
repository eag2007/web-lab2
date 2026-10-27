export const loadPoints = async () => {
  const response = await fetch('http://localhost:8888/api/points/get');

  if (!response.ok) {
    throw new Error(`Статус ${response.status}`);
  }

  return await response.json();
};

export const clearPoints = async () => {
  const response = await fetch('http://localhost:8888/api/points/clear', {
    method: 'POST',
  });

  if (!response.ok) {
    throw new Error(`Статус ${response.status}`);
  }
};
