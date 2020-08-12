(function () {
    'use strict';

    angular
        .module('flairbiApp')
        .factory('GenerateChordDiagram', GenerateChordDiagram);

    GenerateChordDiagram.$inject = ['VisualizationUtils', '$rootScope', 'D3Utils', 'filterParametersService'];

    function GenerateChordDiagram(VisualizationUtils, $rootScope, D3Utils, filterParametersService) {
        return {
            build: function (record, element, panel, isNotification, isIframe) {
                if ((!record.data) || ((record.data instanceof Array) && (!record.data.length))) {
                    element.css({
                        'display': 'flex',
                        'align-items': 'center',
                        'justify-content': 'center'
                    });
                    element[0].innerHTML = '<i class="fa fa-exclamation-circle noDataFound" aria-hidden="true"></i> <p class="noDataText">  No data found with current filters</p>';
                    return;
                }
                function getProperties(VisualizationUtils, record) {
                    var result = {};

                    var features = VisualizationUtils.getDimensionsAndMeasures(record.fields),
                        dimensions = features.dimensions,
                        measures = features.measures;

                    result['showLabels'] = VisualizationUtils.getFieldPropertyValue(dimensions[0], 'Show Labels');
                    result['labelColor'] = VisualizationUtils.getFieldPropertyValue(dimensions[0], 'Colour of labels');
                    result['fontStyle'] = VisualizationUtils.getFieldPropertyValue(dimensions[0], 'Font style');
                    result['fontWeight'] = VisualizationUtils.getFieldPropertyValue(dimensions[0], 'Font weight');
                    result['fontSize'] = parseInt(VisualizationUtils.getFieldPropertyValue(dimensions[0], 'Font size'));
                    result['colorPattern'] = VisualizationUtils.getFieldPropertyValue(measures[0], 'Color Pattern').toLowerCase().replace(' ', '_');
                    result['numberFormat'] = VisualizationUtils.getFieldPropertyValue(measures[0], 'Number format');


                    result['dimension'] = D3Utils.getNames(dimensions);
                    result['dimensionType'] = D3Utils.getTypes(dimensions);

                    result['measure'] = D3Utils.getNames(measures)[0];

                    return result;
                }

                function createChart() {
                    $(element[0]).html('')
                    $(element[0]).append('<div height="' + element[0].clientHeight + '" width="' + element[0].clientWidth + '" style="width:' + element[0].clientWidth + 'px; height:' + element[0].clientHeight + 'px;overflow:hidden;text-align:center;position:relative" vizID=' + record.id + ' id="clusteredhorizontalbar-' + element[0].id + '" ></div>')
                    var div = $('#clusteredhorizontalbar-' + element[0].id)

                    var chorddiagram = flairVisualizations.chorddiagram()
                        .config(getProperties(VisualizationUtils, record))
                        .tooltip(true)
                        .broadcast($rootScope)
                        .filterParameters(filterParametersService)
                        .notification(isNotification == true ? true : false)
                        .print(false)
                        .data(record.data);

                    chorddiagram(div[0])
                    return chorddiagram;

                }
                 if (isNotification || isIframe) {
                    createChart();
                }
                else {
                    if (Object.keys($rootScope.updateWidget).indexOf(record.id) != -1) {
                        if ($rootScope.filterSelection.id != record.id) {
                            var chorddiagram = $rootScope.updateWidget[record.id];

                            chorddiagram.isLiveEnabled(record.isLiveEnabled)
                                .config(getProperties(VisualizationUtils, record))
                                .update(record.data);
                        }
                    } else {
                        var chorddiagram = createChart();
                        $rootScope.updateWidget[record.id] = chorddiagram;
                    }
                }
            }
        }
    }
})();