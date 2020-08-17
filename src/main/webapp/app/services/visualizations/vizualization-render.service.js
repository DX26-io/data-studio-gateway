(function () {
    "use strict";

    /**
     * Container that contains currently active Widgets
     *
     * Flair-bi page is responsible for modifying this container, but rest of the application can use it.
     */
    angular
        .module("flairbiApp")
        .factory("visualizationRenderService", visualizationRenderService);

    visualizationRenderService.$inject = [
        'GenerateStackedverticalbarChart',
        'GenerateStackedhorizontalbarChart',
        'GenerateClusteredverticalbarChart',
        'GenerateClusteredhorizontalbarChart',
        'GenerateLineChart',
        'GeneratePieChart',
        'GenerateDoughnutChart',
        'GenerateComboChart',
        'GenerateGaugePlot',
        'GenerateInfoGraphic',
        'GenerateKPI',
        'GenerateTable',
        'GenerateMap',
        'GenerateScatterPlot',
        'GenerateTreemap',
        'GenerateHeatmap',
        'GenerateBoxplot',
        'GenerateTextObject',
        'GenerateBulletChart',
        'GenerateChordDiagram',
        'GenerateWordCloud',
        'GenerateSankey',
        'GeneratePivotTable',
        'proxyService',
        'filterParametersService',
        'GenerateIframe',
        'GenerateDateRange',
        'GeneratePieGridChart',
        'GenerateNumberGridChart'
        //,'stompClientService'
    ];

    function visualizationRenderService(
        GenerateStackedverticalbarChart,
        GenerateStackedhorizontalbarChart,
        GenerateClusteredverticalbarChart,

        GenerateClusteredhorizontalbarChart,
        GenerateLineChart,
        GeneratePieChart,
        GenerateDoughnutChart,
        GenerateComboChart,
        GenerateGaugePlot,
        GenerateInfoGraphic,
        GenerateKPI,
        GenerateTable,
        GenerateMap,
        GenerateScatterPlot,
        GenerateTreemap,
        GenerateHeatmap,
        GenerateBoxplot,
        GenerateTextObject,
        GenerateBulletChart,
        GenerateChordDiagram,
        GenerateWordCloud,
        GenerateSankey,
        GeneratePivotTable,
        proxyService,
        filterParametersService,
        GenerateIframe,
        GenerateDateRange,
        GeneratePieGridChart,
        GenerateNumberGridChart) {

        var vm = this;
        var widgets = [];

        return {
            setMetaData: setMetaData
        };

        function addWidgets() {
            widgets.GenerateStackedverticalbarChart = GenerateStackedverticalbarChart;
            widgets.GenerateStackedhorizontalbarChart = GenerateStackedhorizontalbarChart;
            widgets.GenerateClusteredverticalbarChart = GenerateClusteredverticalbarChart;

            widgets.GenerateClusteredhorizontalbarChart = GenerateClusteredhorizontalbarChart;
            widgets.GenerateLineChart = GenerateLineChart;
            widgets.GeneratePieChart = GeneratePieChart;
            widgets.GenerateDoughnutChart = GenerateDoughnutChart;
            widgets.GenerateComboChart = GenerateComboChart;
            widgets.GenerateGaugePlot = GenerateGaugePlot;
            widgets.GenerateInfoGraphic = GenerateInfoGraphic;
            widgets.GenerateMap = GenerateMap;
            widgets.GenerateScatterPlot = GenerateScatterPlot;
            widgets.GenerateKPI = GenerateKPI;
            widgets.GenerateTable = GenerateTable;
            widgets.GenerateTreemap = GenerateTreemap;
            widgets.GenerateHeatmap = GenerateHeatmap;
            widgets.GenerateBoxplot = GenerateBoxplot;
            widgets.GenerateTextObject = GenerateTextObject;
            widgets.GenerateBulletChart = GenerateBulletChart;
            widgets.GenerateChordDiagram = GenerateChordDiagram;
            widgets.GenerateWordCloud = GenerateWordCloud;
            widgets.GenerateSankey = GenerateSankey;
            widgets.GeneratePivotTable = GeneratePivotTable;
            widgets.GenerateIframe = GenerateIframe;
            widgets.GenerateDateRange = GenerateDateRange;
            widgets.GeneratePieGridChart = GeneratePieGridChart;
            widgets.GenerateNumberGridChart = GenerateNumberGridChart;
        }


        function createWidget(visualMetadata, contentId, isNotification,isIframe) {
            var widgetId = '#' + contentId;
            var el = $(widgetId);
            var width = el.width(),
                height = el.height();
            var panel = $('.grid-stack');
            var widget = widgets[vm.widget];

            widgets[vm.widget].build(
                visualMetadata,
                el,
                panel,
                isNotification,
                isIframe
            );
        }

        function setMetaData(v, metadata, contentId, isNotification,isIframe) {
            if (v) {
                addWidgets();
                vm.data = v;
                vm.data.data = metadata.data;
                vm.widget = v.metadataVisual.functionname;
                if(vm.widget==="GenerateIframe"){
                    createWidget(vm.data, contentId, isNotification,true);
                }
                else{
                    createWidget(vm.data, contentId, isNotification,isIframe);
                }
            }
        }


    }
})();
