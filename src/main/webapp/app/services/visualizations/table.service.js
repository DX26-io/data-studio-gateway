(function () {
    'use strict';

    angular
        .module('flairbiApp')
        .factory('GenerateTable', GenerateTable);

    GenerateTable.$inject = ['VisualizationUtils', '$rootScope', 'D3Utils', 'filterParametersService'];

    function GenerateTable(VisualizationUtils, $rootScope, D3Utils, filterParametersService) {
        return {
            build: function (record, element, panel, isNotification, isIframe) {
                if ($rootScope.activePage.activePageNo === 0) {
                    if ((!record.data) || ((record.data instanceof Array) && (!record.data.length))) {
                        element.css({
                            'display': 'flex',
                            'align-items': 'center',
                            'justify-content': 'center'
                        });
                        element[0].innerHTML = '<i class="fa fa-exclamation-circle noDataFound" aria-hidden="true"></i> <p class="noDataText">  No data found with current filters</p>';
                        return;
                    }
                }

                function getProperties(VisualizationUtils, record) {
                    var result = {};

                    var features = VisualizationUtils.getDimensionsAndMeasures(record.fields),
                        dimensions = features.dimensions,
                        measures = features.measures;

                    result['dimension'] = D3Utils.getNames(dimensions);
                    result['dimensionType'] = D3Utils.getTypes(dimensions);
                    result['measure'] = D3Utils.getNames(measures);

                    result['maxDim'] = dimensions.length;
                    result['maxMes'] = measures.length;
                    result['showTotal'] = VisualizationUtils.getPropertyValue(record.properties, 'Show Total');
                    result["displayNameForDimension"] = [];
                    result["cellColorForDimension"] = [];
                    result["fontStyleForDimension"] = [];
                    result["fontWeightForDimension"] = [];
                    result["fontSizeForDimension"] = [];
                    result["textColorForDimension"] = [];
                    result["textColorExpressionForDimension"] = [];
                    result["textAlignmentForDimension"] = [];

                    result["displayNameForMeasure"] = [];
                    result["cellColorForMeasure"] = [];
                    result["cellColorExpressionForMeasure"] = [];
                    result["fontStyleForMeasure"] = [];
                    result["fontSizeForMeasure"] = [];
                    result["fontWeightForMeasure"] = [];
                    result["numberFormatForMeasure"] = [];
                    result["textColorForMeasure"] = [];
                    result["textAlignmentForMeasure"] = [];
                    result["textColorExpressionForMeasure"] = [];
                    result["iconNameForMeasure"] = [];
                    result["iconFontWeight"] = [];
                    result["iconColor"] = [];
                    result["iconPositionForMeasure"] = [];
                    result["iconExpressionForMeasure"] = [];

                    result['limit'] = VisualizationUtils.getPropertyValue(record.properties, 'Limit');


                    for (var i = 0; i < result.maxDim; i++) {
                        result['displayNameForDimension'].push(
                            VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Display name') ||
                            result['dimension'][i]
                        );
                        result['cellColorForDimension'].push(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Cell colour'));
                        result['fontStyleForDimension'].push(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Font style'));
                        result['fontWeightForDimension'].push(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Font weight'));
                        result['fontSizeForDimension'].push(parseInt(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Font size')));
                        result['textColorForDimension'].push(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Text colour'));
                        result['textColorExpressionForDimension'].push(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Text colour expression'));
                        result['textAlignmentForDimension'].push(VisualizationUtils.getFieldPropertyValue(dimensions[i], 'Alignment'));
                    }

                    for (var i = 0; i < result.maxMes; i++) {
                        result['displayNameForMeasure'].push(
                            VisualizationUtils.getFieldPropertyValue(measures[i], 'Display name') ||
                            result['measure'][i]
                        );
                        result['cellColorForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Cell colour'));
                        result['cellColorExpressionForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Cell colour expression'));
                        result['fontStyleForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Font style'));
                        result['fontWeightForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Font weight'));
                        result['fontSizeForMeasure'].push(parseInt(VisualizationUtils.getFieldPropertyValue(measures[i], 'Font size')));
                        result['numberFormatForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Number format'));
                        result['textColorForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Text colour'));
                        result['textAlignmentForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Text alignment').toLowerCase());
                        result['textColorExpressionForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Text colour expression'));
                        result['iconNameForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Icon name'));
                        result['iconPositionForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Icon position'));
                        result['iconExpressionForMeasure'].push(VisualizationUtils.getFieldPropertyValue(measures[i], 'Icon Expression'));
                    }

                    return result;
                }
                var config = getProperties(VisualizationUtils, record);

                function createChart() {
                    $(element[0]).html('')
                    $(element[0]).append('<div height="' + element[0].clientHeight + '" width="' + element[0].clientWidth + '" style="width:' + element[0].clientWidth + 'px; height:' + element[0].clientHeight + 'px;overflow:hidden;text-align:center;position:relative" id="table-' + element[0].id + '" ></div>')
                    var div = $('#table-' + element[0].id)

                    var table = flairVisualizations.table()
                        .config(config)
                        .broadcast($rootScope)
                        .filterParameters(filterParametersService)
                        .print(isNotification == true ? true : false)
                        .notification(isNotification == true ? true : false)
                        .data(record.data);

                    table(div[0])

                    return table;
                }
                 if (isNotification || isIframe) {
                    createChart();
                }
                else {
                    if (Object.keys($rootScope.updateWidget).indexOf(record.id) != -1) {
                        if ($rootScope.filterSelection.id != record.id) {

                            var table = $rootScope.updateWidget[record.id];
                            table
                                .config(config)
                                .update(record.data);
                        }
                    } else {
                        var table = createChart();
                        $rootScope.updateWidget[record.id] = table;
                    }
                }
            }
        }
    }
})();