export const loadPoints = async () => {
  const response = await fetch('api/points/get');

  if (!response.ok) {
    throw new Error((await response.text()) || `Статус ${response.status}`);
  }

  return await response.json();
};

export const clearPoints = async () => {
  const response = await fetch('api/points/clear', {
    method: 'POST',
  });

  if (!response.ok) {
    throw new Error((await response.text()) || `Статус ${response.status}`);
  }
};
