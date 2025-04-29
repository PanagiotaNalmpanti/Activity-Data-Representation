const category = document.getElementById('category');
const timeInterval = document.getElementById('timeInterval');

category.addEventListener('change', () => {
    const selection = category.value;
    if (selection === 'Distance' || selection === 'HeartRate' || selection === 'Sleep' ||
        selection === 'Weight' || selection === 'VeryActiveMin' || selection === 'FairlyActiveMin' ||
        selection === 'LightlyActiveMin' || selection === 'SedentaryMin') {
        timeInterval.value = 'daily';
        timeInterval.disabled = true;
    }
    else {
        timeInterval.disabled = false;
        timeInterval.value = '';
    }
});