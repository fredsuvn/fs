/**
 * JMH Visualizer - Main JavaScript module
 * Handles file upload, data processing and chart rendering
 */

document.addEventListener('DOMContentLoaded', function() {
    // DOM elements
    const fileInput = document.getElementById('fileInput');
    const uploadBtn = document.getElementById('uploadBtn');
    const uploadArea = document.getElementById('uploadArea');
    const chartsContainer = document.getElementById('chartsContainer');
    const noDataMessage = document.getElementById('noDataMessage');
    const errorMessage = document.getElementById('errorMessage');
    const fileInfo = document.getElementById('fileInfo');

    // Application state
    let chartData = null;

//    // Event listeners
//    uploadBtn.addEventListener('click', () => fileInput.click());
//    fileInput.addEventListener('change', handleFileSelect);
//
//    // Drag and drop events
//    uploadArea.addEventListener('dragover', (e) => {
//        e.preventDefault();
//        uploadArea.classList.add('highlight');
//    });
//
//    uploadArea.addEventListener('dragleave', () => {
//        uploadArea.classList.remove('highlight');
//    });
//
//    uploadArea.addEventListener('drop', (e) => {
//        e.preventDefault();
//        uploadArea.classList.remove('highlight');
//
//        if (e.dataTransfer.files.length) {
//            handleFile(e.dataTransfer.files[0]);
//        }
//    });

    /**
     * Handle file selection from input
     */
    function handleFileSelect(e) {
        if (e.target.files.length) {
            handleFile(e.target.files[0]);
        }
    }

    /**
     * Process the selected file
     */
    function handleFile(file) {
        // Reset UI state
        errorMessage.style.display = 'none';
        fileInfo.style.display = 'none';

        // Validate file type
        if (!file.name.toLowerCase().endsWith('.json')) {
            showError('Please select a JSON format file');
            return;
        }

        // Display file information
        fileInfo.innerHTML = `Selected file: <strong>${file.name}</strong> (${formatFileSize(file.size)})`;
        fileInfo.style.display = 'block';

        // Read file content
        const reader = new FileReader();

        reader.onload = function(e) {
            try {
                const jsonData = JSON.parse(e.target.result);
                processJMHData(jsonData);
            } catch (error) {
                showError('Error parsing JSON file: ' + error.message);
            }
        };

        reader.onerror = function() {
            showError('Error reading file');
        };

        reader.readAsText(file);
    }

    /**
     * Process JMH JSON data and render charts
     */
    function processJMHData(data) {
        if (!Array.isArray(data)) {
            showError('Invalid JMH data format, expected array format');
            return;
        }

        chartData = data;
        renderAllCharts(data);
    }

    /**
     * Render all charts grouped by benchmark method
     */
    function renderAllCharts(data) {
        // Group data by benchmark method
        const groupedData = groupByBenchmark(data);

        // Clear container
        chartsContainer.innerHTML = '';
        noDataMessage.style.display = 'none';

        if (Object.keys(groupedData).length === 0) {
            noDataMessage.style.display = 'block';
            return;
        }

        // Create chart for each benchmark method
        Object.keys(groupedData).forEach(benchmark => {
            createChartGroup(groupedData[benchmark], benchmark);
        });
    }

    /**
     * Group benchmark data by method name
     */
    function groupByBenchmark(data) {
        const groups = {};

        data.forEach(item => {
            if (item.benchmark && item.primaryMetric) {
                // Extract class name (without package) and method name
                const parts = item.benchmark.split('.');
                const className = parts[parts.length - 2]; // Second last part is class name
                const methodName = parts[parts.length - 1]; // Last part is method name
                const benchmarkKey = `${className}.${methodName}`;

                if (!groups[benchmarkKey]) {
                    groups[benchmarkKey] = [];
                }

                groups[benchmarkKey].push(item);
            }
        });

        return groups;
    }

    /**
     * Create a chart group with title and controls
     */
    function createChartGroup(data, benchmarkName) {
        const chartGroup = document.createElement('div');
        chartGroup.className = 'chart-group';

        // Create header with title and controls
        const headerDiv = document.createElement('div');
        headerDiv.className = 'chart-header';

        // Title
        const titleDiv = document.createElement('div');
        titleDiv.className = 'chart-title';
        titleDiv.textContent = benchmarkName;
        headerDiv.appendChild(titleDiv);

        // Controls
        const controlsDiv = document.createElement('div');
        controlsDiv.className = 'chart-controls';

        // Chart type controls
        const chartTypeGroup = document.createElement('div');
        chartTypeGroup.className = 'chart-control-group';

        const chartTypeLabel = document.createElement('span');
        chartTypeLabel.className = 'chart-control-label';
        chartTypeLabel.textContent = 'Chart:';
        chartTypeGroup.appendChild(chartTypeLabel);

        const horizontalBtn = document.createElement('button');
        horizontalBtn.className = 'chart-control-btn active';
        horizontalBtn.textContent = 'Horizontal';
        horizontalBtn.dataset.chartType = 'horizontal';

        const verticalBtn = document.createElement('button');
        verticalBtn.className = 'chart-control-btn';
        verticalBtn.textContent = 'Vertical';
        verticalBtn.dataset.chartType = 'vertical';

        chartTypeGroup.appendChild(horizontalBtn);
        chartTypeGroup.appendChild(verticalBtn);
        controlsDiv.appendChild(chartTypeGroup);

        // Sort controls
        const sortGroup = document.createElement('div');
        sortGroup.className = 'chart-control-group';

        const sortLabel = document.createElement('span');
        sortLabel.className = 'chart-control-label';
        sortLabel.textContent = 'Sort:';
        sortGroup.appendChild(sortLabel);

        const scoreAscBtn = document.createElement('button');
        scoreAscBtn.className = 'chart-control-btn';
        scoreAscBtn.textContent = 'Score ↑';
        scoreAscBtn.dataset.sortType = 'score-asc';

        const scoreDescBtn = document.createElement('button');
        scoreDescBtn.className = 'chart-control-btn';
        scoreDescBtn.textContent = 'Score ↓';
        scoreDescBtn.dataset.sortType = 'score-desc';

        const nameAscBtn = document.createElement('button');
        nameAscBtn.className = 'chart-control-btn';
        nameAscBtn.textContent = 'Name ↑';
        nameAscBtn.dataset.sortType = 'name-asc';

        const nameDescBtn = document.createElement('button');
        nameDescBtn.className = 'chart-control-btn';
        nameDescBtn.textContent = 'Name ↓';
        nameDescBtn.dataset.sortType = 'name-desc';

        sortGroup.appendChild(scoreAscBtn);
        sortGroup.appendChild(scoreDescBtn);
        sortGroup.appendChild(nameAscBtn);
        sortGroup.appendChild(nameDescBtn);
        controlsDiv.appendChild(sortGroup);

        headerDiv.appendChild(controlsDiv);
        chartGroup.appendChild(headerDiv);

        // Create chart container
        const chartDiv = document.createElement('div');
        chartDiv.className = 'chart-container';
        chartGroup.appendChild(chartDiv);

        chartsContainer.appendChild(chartGroup);

        // Store current state for this chart
        const chartState = {
            data: data,
            chartType: 'horizontal',
            sortType: 'original'
        };

        // Render initial chart (horizontal by default)
        renderChart(data, chartDiv, chartState);

        // Add event listeners to chart type buttons
        horizontalBtn.addEventListener('click', function() {
            horizontalBtn.classList.add('active');
            verticalBtn.classList.remove('active');
            chartState.chartType = 'horizontal';
            renderChart(data, chartDiv, chartState);
        });

        verticalBtn.addEventListener('click', function() {
            verticalBtn.classList.add('active');
            horizontalBtn.classList.remove('active');
            chartState.chartType = 'vertical';
            renderChart(data, chartDiv, chartState);
        });

        // Add event listeners to sort buttons
        const sortButtons = [scoreAscBtn, scoreDescBtn, nameAscBtn, nameDescBtn];
        sortButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                // Remove active class from all sort buttons
                sortButtons.forEach(b => b.classList.remove('active'));
                // Add active class to clicked button
                this.classList.add('active');

                chartState.sortType = this.dataset.sortType;
                renderChart(data, chartDiv, chartState);
            });
        });
    }

    /**
     * Sort data based on sort type
     */
    function sortData(data, sortType) {
        if (sortType === 'original') {
            return data;
        }

        const sortedData = [...data];

        switch(sortType) {
            case 'score-asc':
                sortedData.sort((a, b) => a.primaryMetric.score - b.primaryMetric.score);
                break;
            case 'score-desc':
                sortedData.sort((a, b) => b.primaryMetric.score - a.primaryMetric.score);
                break;
            case 'name-asc':
                sortedData.sort((a, b) => {
                    const aName = getParamDisplayName(a);
                    const bName = getParamDisplayName(b);
                    return aName.localeCompare(bName);
                });
                break;
            case 'name-desc':
                sortedData.sort((a, b) => {
                    const aName = getParamDisplayName(a);
                    const bName = getParamDisplayName(b);
                    return bName.localeCompare(aName);
                });
                break;
        }

        return sortedData;
    }

    /**
     * Get parameter display name with aligned formatting
     */
    function getParamDisplayName(item, allParamKeys, maxKeyLengths) {
        if (!item.params || Object.keys(item.params).length === 0) {
            return 'Default';
        }

        const paramParts = allParamKeys.map(key => {
            const value = item.params[key] || '-';
            const keyLength = maxKeyLengths[key] || key.length;
            const paddedKey = key.padEnd(keyLength, ' ');
            return `${paddedKey} = ${value}`;
        });

        return paramParts.join(', ');
    }

    /**
     * Calculate maximum key lengths for alignment
     */
    function calculateMaxKeyLengths(data) {
        const maxKeyLengths = {};

        data.forEach(item => {
            if (item.params) {
                Object.keys(item.params).forEach(key => {
                    if (!maxKeyLengths[key] || key.length > maxKeyLengths[key]) {
                        maxKeyLengths[key] = key.length;
                    }
                });
            }
        });

        return maxKeyLengths;
    }

    /**
     * Render a single chart with the given data and type
     */
    function renderChart(data, chartDom, chartState) {
        // Sort data if needed
        const sortedData = sortData(data, chartState.sortType);

        // Find all parameter keys to ensure consistent formatting
        const allParamKeys = [];
        sortedData.forEach(item => {
            if (item.params) {
                Object.keys(item.params).forEach(key => {
                    if (!allParamKeys.includes(key)) {
                        allParamKeys.push(key);
                    }
                });
            }
        });

        // Sort parameter keys for consistent display
        allParamKeys.sort();

        // Calculate maximum key lengths for alignment
        const maxKeyLengths = calculateMaxKeyLengths(sortedData);

        // Prepare chart data
        const paramNames = [];
        const scores = [];
        const errors = [];
        const paramsList = [];

        sortedData.forEach(item => {
            // Generate parameter display name with aligned formatting
            const paramName = getParamDisplayName(item, allParamKeys, maxKeyLengths);

            paramNames.push(paramName);
            scores.push(item.primaryMetric.score);
            errors.push(item.primaryMetric.scoreError || 0);
            paramsList.push(item.params || {});
        });

        if (paramNames.length === 0) {
            return;
        }

        // Initialize ECharts instance
        const chart = echarts.init(chartDom);

        let option;

        if (chartState.chartType === 'vertical') {
            // Vertical bar chart
            option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function(params) {
                        const data = params[0];
                        let paramsText = 'No parameters';

                        if (paramsList[data.dataIndex] && Object.keys(paramsList[data.dataIndex]).length > 0) {
                            paramsText = allParamKeys.map(key => {
                                const value = paramsList[data.dataIndex][key] || '-';
                                const keyLength = maxKeyLengths[key] || key.length;
                                const paddedKey = key.padEnd(keyLength, ' ');
                                return `<div style="font-family: monospace; margin: 2px 0;">${paddedKey} = ${value}</div>`;
                            }).join('');
                        }

                        return `
                            <div style="font-weight: bold; margin-bottom: 5px;">${data.name}</div>
                            <div>Score: <b>${data.value}</b></div>
                            <div>Error: ±${errors[data.dataIndex]}</div>
                            <div style="margin-top: 5px;">Parameters:</div>
                            ${paramsText}
                        `;
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '15%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: paramNames,
                    axisLabel: {
                        interval: 0,
                        rotate: 30,
                        fontSize: 12,
                        fontFamily: 'monospace'
                    }
                },
                yAxis: {
                    type: 'value',
                    name: 'Score'
                },
                series: [
                    {
                        name: 'Score',
                        type: 'bar',
                        data: scores,
                        itemStyle: {
                            color: function(params) {
                                const colorList = [
                                    '#5470c6', '#91cc75', '#fac858', '#ee6666',
                                    '#73c0de', '#3ba272', '#fc8452', '#9a60b4'
                                ];
                                return colorList[params.dataIndex % colorList.length];
                            }
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{c}'
                        }
                    }
                ]
            };
        } else {
            // Horizontal bar chart
            option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function(params) {
                        const data = params[0];
                        let paramsText = 'No parameters';

                        if (paramsList[data.dataIndex] && Object.keys(paramsList[data.dataIndex]).length > 0) {
                            paramsText = allParamKeys.map(key => {
                                const value = paramsList[data.dataIndex][key] || '-';
                                const keyLength = maxKeyLengths[key] || key.length;
                                const paddedKey = key.padEnd(keyLength, ' ');
                                return `<div style="font-family: monospace; margin: 2px 0;">${paddedKey} = ${value}</div>`;
                            }).join('');
                        }

                        return `
                            <div style="font-weight: bold; margin-bottom: 5px;">${data.name}</div>
                            <div>Score: <b>${data.value}</b></div>
                            <div>Error: ±${errors[data.dataIndex]}</div>
                            <div style="margin-top: 5px;">Parameters:</div>
                            ${paramsText}
                        `;
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    name: 'Score',
                    nameLocation: 'middle',
                    nameGap: 30
                },
                yAxis: {
                    type: 'category',
                    data: paramNames,
                    axisLabel: {
                        interval: 0,
                        rotate: 0,
                        fontSize: 12,
                        fontFamily: 'monospace'
                    }
                },
                series: [
                    {
                        name: 'Score',
                        type: 'bar',
                        data: scores,
                        itemStyle: {
                            color: function(params) {
                                const colorList = [
                                    '#5470c6', '#91cc75', '#fac858', '#ee6666',
                                    '#73c0de', '#3ba272', '#fc8452', '#9a60b4'
                                ];
                                return colorList[params.dataIndex % colorList.length];
                            }
                        },
                        label: {
                            show: true,
                            position: 'right',
                            formatter: '{c}'
                        }
                    }
                ]
            };
        }

        // Apply configuration and render chart
        chart.setOption(option);

        // Resize chart when window size changes
        window.addEventListener('resize', function() {
            chart.resize();
        });
    }

    /**
     * Display error message
     */
    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
    }

    /**
     * Format file size to human readable format
     */
    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // read results.json
    fetch('results.json')
          .then(response => response.json())
          .then(data => {
          processJMHData(data)
            //console.log('JSON 内容：', data);
            // 可以在这里操作数据（如渲染到页面）
            //document.body.innerHTML = `<h1>${data.name}</h1>`;
          })
          .catch(error => alert('错误：', error));
});