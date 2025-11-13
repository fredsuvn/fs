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
    const uploadSection = document.querySelector('.upload-section');
    const globalSortSelect = document.getElementById('globalSortSelect');

    // Application state
    let chartData = null;
    let chartGroups = [];

    // Initialize the application
    init();

    function init() {
        // Set up event listeners
        uploadBtn.addEventListener('click', () => fileInput.click());
        fileInput.addEventListener('change', handleFileSelect);

        // Set up drag and drop events
        setupDragAndDrop();

        // Set up collapsible sections
        setupCollapsibleSections();

        // Set up global sort
        globalSortSelect.addEventListener('change', applyGlobalSort);

        // Try to load results.json from the same directory
        loadDefaultResults();
    }

    /**
     * Set up drag and drop functionality
     */
    function setupDragAndDrop() {
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('highlight');
        });

        uploadArea.addEventListener('dragleave', () => {
            uploadArea.classList.remove('highlight');
        });

        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('highlight');

            if (e.dataTransfer.files.length) {
                handleFile(e.dataTransfer.files[0]);
            }
        });
    }

    /**
     * Set up collapsible sections
     */
    function setupCollapsibleSections() {
        // Upload section
        const uploadHeader = uploadSection.querySelector('.collapsible-header');
        const uploadContent = uploadSection.querySelector('.collapsible-content');

        uploadHeader.addEventListener('click', () => {
            const isVisible = uploadContent.style.display !== 'none';
            uploadContent.style.display = isVisible ? 'none' : 'block';
            uploadHeader.classList.toggle('collapsed', isVisible);
        });

        // Initially collapse upload section
        uploadContent.style.display = 'none';
        uploadHeader.classList.add('collapsed');
    }

    /**
     * Try to load results.json from the same directory
     */
    function loadDefaultResults() {
        fetch('results.json')
            .then(response => {
                if (!response.ok) {
                    throw new Error('results.json not found');
                }
                return response.json();
            })
            .then(data => {
                fileInfo.innerHTML = 'Loaded default file: <strong>results.json</strong>';
                fileInfo.style.display = 'block';
                processJMHData(data);
            })
            .catch(error => {
                console.log('Could not load results.json:', error.message);
                // Don't show error, just continue without default file
            });
    }

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
     * Render all charts grouped by class and method
     */
    function renderAllCharts(data) {
        // Group data by class and method
        const groupedData = groupByClassAndMethod(data);

        // Clear container
        chartsContainer.innerHTML = '';
        noDataMessage.style.display = 'none';
        chartGroups = [];

        if (Object.keys(groupedData).length === 0) {
            noDataMessage.style.display = 'block';
            return;
        }

        // Create class groups
        Object.keys(groupedData).forEach(className => {
            createClassGroup(groupedData[className], className);
        });

        // Apply global sort if set
        if (globalSortSelect.value !== 'score-desc') {
            applyGlobalSort();
        }
    }

    /**
     * Group benchmark data by class and method
     */
    function groupByClassAndMethod(data) {
        const groups = {};

        data.forEach(item => {
            if (item.benchmark && item.primaryMetric) {
                // Extract class name and method name
                const parts = item.benchmark.split('.');
                const className = parts[parts.length - 2]; // Second last part is class name
                const methodName = parts[parts.length - 1]; // Last part is method name

                if (!groups[className]) {
                    groups[className] = {};
                }

                if (!groups[className][methodName]) {
                    groups[className][methodName] = [];
                }

                groups[className][methodName].push(item);
            }
        });

        return groups;
    }

    /**
     * Create a class group with methods
     */
    function createClassGroup(classData, className) {
        const classGroup = document.createElement('div');
        classGroup.className = 'class-group';

        // Create class header
        const classHeader = document.createElement('div');
        classHeader.className = 'class-header';

        const classTitle = document.createElement('div');
        classTitle.className = 'class-title';

        const collapseIcon = document.createElement('span');
        collapseIcon.className = 'collapse-icon';
        collapseIcon.textContent = '▼';
        classTitle.appendChild(collapseIcon);

        const titleText = document.createElement('span');
        titleText.textContent = className;
        classTitle.appendChild(titleText);

        classHeader.appendChild(classTitle);
        classGroup.appendChild(classHeader);

        // Create class content
        const classContent = document.createElement('div');
        classContent.className = 'class-content';
        classGroup.appendChild(classContent);

        chartsContainer.appendChild(classGroup);

        // Set up collapsible functionality for class group
        classHeader.addEventListener('click', () => {
            const isVisible = classContent.style.display !== 'none';
            classContent.style.display = isVisible ? 'none' : 'block';
            classHeader.classList.toggle('collapsed', isVisible);
        });

        // Create method groups for this class
        Object.keys(classData).forEach(methodName => {
            createMethodGroup(classData[methodName], className, methodName, classContent);
        });
    }

    /**
     * Create a method group with chart and controls
     */
    function createMethodGroup(data, className, methodName, classContent) {
        const methodGroup = document.createElement('div');
        methodGroup.className = 'method-group';

        // Create method header
        const methodHeader = document.createElement('div');
        methodHeader.className = 'method-header';

        const methodTitle = document.createElement('div');
        methodTitle.className = 'method-title';

        const collapseIcon = document.createElement('span');
        collapseIcon.className = 'collapse-icon';
        collapseIcon.textContent = '▼';
        methodTitle.appendChild(collapseIcon);

        const titleText = document.createElement('span');
        titleText.textContent = methodName;
        methodTitle.appendChild(titleText);

        methodHeader.appendChild(methodTitle);

        // Controls
        const methodControls = document.createElement('div');
        methodControls.className = 'method-controls';

        // Chart type controls
        const chartTypeGroup = document.createElement('div');
        chartTypeGroup.className = 'control-group';

        const chartTypeLabel = document.createElement('span');
        chartTypeLabel.className = 'control-label';
        chartTypeLabel.textContent = 'Chart:';
        chartTypeGroup.appendChild(chartTypeLabel);

        const chartTypeSelect = document.createElement('select');
        chartTypeSelect.className = 'control-select';
        chartTypeSelect.innerHTML = `
            <option value="horizontal">Horizontal Bar</option>
            <option value="vertical">Vertical Bar</option>
        `;
        chartTypeGroup.appendChild(chartTypeSelect);
        methodControls.appendChild(chartTypeGroup);

        // Metric controls
        const metricGroup = document.createElement('div');
        metricGroup.className = 'control-group';

        const metricLabel = document.createElement('span');
        metricLabel.className = 'control-label';
        metricLabel.textContent = 'Metric:';
        metricGroup.appendChild(metricLabel);

        const metricSelect = document.createElement('select');
        metricSelect.className = 'control-select';
        metricSelect.innerHTML = `
            <option value="score">Score</option>
            <option value="throughput">Throughput</option>
        `;
        metricGroup.appendChild(metricSelect);
        methodControls.appendChild(metricGroup);

        // Sort controls
        const sortGroup = document.createElement('div');
        sortGroup.className = 'control-group';

        const sortLabel = document.createElement('span');
        sortLabel.className = 'control-label';
        sortLabel.textContent = 'Sort:';
        sortGroup.appendChild(sortLabel);

        const sortSelect = document.createElement('select');
        sortSelect.className = 'control-select';
        sortSelect.innerHTML = `
            <option value="score-desc" selected>Score ↓</option>
            <option value="score-asc">Score ↑</option>
            <option value="param-asc">Parameter ↑</option>
            <option value="param-desc">Parameter ↓</option>
        `;
        sortGroup.appendChild(sortSelect);
        methodControls.appendChild(sortGroup);

        methodHeader.appendChild(methodControls);
        methodGroup.appendChild(methodHeader);

        // Create method container
        const methodContainer = document.createElement('div');
        methodContainer.className = 'method-container';
        methodGroup.appendChild(methodContainer);

        classContent.appendChild(methodGroup);

        // Set up collapsible functionality for method group
        methodHeader.addEventListener('click', (e) => {
            // Don't collapse if clicking on controls
            if (e.target.tagName === 'SELECT' || e.target.tagName === 'OPTION') {
                return;
            }

            const isVisible = methodContainer.style.display !== 'none';
            methodContainer.style.display = isVisible ? 'none' : 'block';
            methodHeader.classList.toggle('collapsed', isVisible);
        });

        // Store current state for this chart
        const chartState = {
            data: data,
            chartType: 'horizontal',
            metric: 'score',
            sortType: 'score-desc',
            methodContainer: methodContainer,
            chartTypeSelect: chartTypeSelect,
            metricSelect: metricSelect,
            sortSelect: sortSelect
        };

        // Save to chart groups array
        chartGroups.push(chartState);

        // Render initial chart
        renderChart(chartState);

        // Add event listeners to controls
        chartTypeSelect.addEventListener('change', function() {
            chartState.chartType = this.value;
            renderChart(chartState);
        });

        metricSelect.addEventListener('change', function() {
            chartState.metric = this.value;
            renderChart(chartState);
        });

        sortSelect.addEventListener('change', function() {
            chartState.sortType = this.value;
            renderChart(chartState);
        });
    }

    /**
     * Apply global sort to all charts
     */
    function applyGlobalSort() {
        const globalSortType = globalSortSelect.value;

        chartGroups.forEach(chartState => {
            // Update the individual sort select to match global
            chartState.sortSelect.value = globalSortType;
            chartState.sortType = globalSortType;
            renderChart(chartState);
        });
    }

    /**
     * Sort data based on sort type
     */
    function sortData(data, sortType, metric) {
        if (sortType === 'original') {
            return data;
        }

        const sortedData = [...data];

        switch(sortType) {
            case 'score-asc':
                sortedData.sort((a, b) => getMetricValue(a, metric) - getMetricValue(b, metric));
                break;
            case 'score-desc':
                sortedData.sort((a, b) => getMetricValue(b, metric) - getMetricValue(a, metric));
                break;
            case 'param-asc':
                sortedData.sort((a, b) => {
                    const aName = getParamDisplayName(a);
                    const bName = getParamDisplayName(b);
                    return aName.localeCompare(bName);
                });
                break;
            case 'param-desc':
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
     * Get metric value based on selected metric type
     */
    function getMetricValue(item, metric) {
        if (metric === 'throughput') {
            // Check if throughput metric exists in secondaryMetrics
            if (item.secondaryMetrics && item.secondaryMetrics.thrpt) {
                return item.secondaryMetrics.thrpt.score;
            }
            // Fall back to primary metric score if throughput not available
            return item.primaryMetric.score;
        }

        // Default to score
        return item.primaryMetric.score;
    }

    /**
     * Get metric error based on selected metric type
     */
    function getMetricError(item, metric) {
        if (metric === 'throughput') {
            // Check if throughput metric exists in secondaryMetrics
            if (item.secondaryMetrics && item.secondaryMetrics.thrpt) {
                return item.secondaryMetrics.thrpt.scoreError || 0;
            }
            // Fall back to primary metric error if throughput not available
            return item.primaryMetric.scoreError || 0;
        }

        // Default to score error
        return item.primaryMetric.scoreError || 0;
    }

    /**
     * Get metric unit based on selected metric type
     */
    function getMetricUnit(item, metric) {
        if (metric === 'throughput') {
            // Check if throughput metric exists in secondaryMetrics
            if (item.secondaryMetrics && item.secondaryMetrics.thrpt) {
                return item.secondaryMetrics.thrpt.scoreUnit || 'ops/s';
            }
            // Fall back to primary metric unit if throughput not available
            return item.primaryMetric.scoreUnit || 'ops/ms';
        }

        // Default to score unit
        return item.primaryMetric.scoreUnit || 'ops/ms';
    }

    /**
     * Get parameter display name with aligned formatting
     */
    function getParamDisplayName(item) {
        if (!item.params || Object.keys(item.params).length === 0) {
            return 'Default';
        }

        const paramParts = Object.entries(item.params).map(([key, value]) => {
            return `${key} = ${value}`;
        });

        return paramParts.join(', ');
    }

    /**
     * Render a single chart with the given data and type
     */
    function renderChart(chartState) {
        const { data, chartType, metric, sortType, methodContainer } = chartState;

        // Sort data if needed
        const sortedData = sortData(data, sortType, metric);

        // Prepare chart data
        const paramNames = [];
        const values = [];
        const errors = [];
        const paramsList = [];

        sortedData.forEach(item => {
            // Generate parameter display name
            const paramName = getParamDisplayName(item);

            paramNames.push(paramName);
            values.push(getMetricValue(item, metric));
            errors.push(getMetricError(item, metric));
            paramsList.push(item.params || {});
        });

        if (paramNames.length === 0) {
            return;
        }

        // Get metric unit for axis labels
        const metricUnit = getMetricUnit(sortedData[0], metric);
        const metricName = metric === 'throughput' ? 'Throughput' : 'Score';

        // Initialize ECharts instance
        const chart = echarts.init(methodContainer);

        let option;

        if (chartType === 'vertical') {
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
                            paramsText = Object.entries(paramsList[data.dataIndex])
                                .map(([key, value]) => {
                                    return `<div style="margin: 2px 0;">${key} = ${value}</div>`;
                                }).join('');
                        }

                        return `
                            <div style="font-weight: bold; margin-bottom: 5px;">${data.name}</div>
                            <div>${metricName}: <b>${data.value}</b> ${metricUnit}</div>
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
                        fontSize: 12
                    }
                },
                yAxis: {
                    type: 'value',
                    name: `${metricName} (${metricUnit})`
                },
                series: [
                    {
                        name: metricName,
                        type: 'bar',
                        data: values,
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
                            paramsText = Object.entries(paramsList[data.dataIndex])
                                .map(([key, value]) => {
                                    return `<div style="margin: 2px 0;">${key} = ${value}</div>`;
                                }).join('');
                        }

                        return `
                            <div style="font-weight: bold; margin-bottom: 5px;">${data.name}</div>
                            <div>${metricName}: <b>${data.value}</b> ${metricUnit}</div>
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
                    name: `${metricName} (${metricUnit})`,
                    nameLocation: 'middle',
                    nameGap: 30
                },
                yAxis: {
                    type: 'category',
                    data: paramNames,
                    axisLabel: {
                        interval: 0,
                        rotate: 0,
                        fontSize: 12
                    }
                },
                series: [
                    {
                        name: metricName,
                        type: 'bar',
                        data: values,
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
});