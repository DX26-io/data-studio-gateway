(function () {
    'use strict';

    angular
        .module('flairbiApp')
        .factory('ReportManagementUtilsService', ReportManagementUtilsService);

    ReportManagementUtilsService.$inject = ['schedulerService', '$rootScope', '$location', '$uibModal', '$state', '$window'];

    function ReportManagementUtilsService(schedulerService, $rootScope, $location, $uibModal, $state, $window) {
        var service = {
            goToBuildPage: goToBuildPage,
            updateReport: updateReport,
            executeNow: executeNow,
            goToViewDataPage: goToViewDataPage
        };

        return service;

        function goToBuildPage(build_url) {
            var buildPage = build_url.split("#")[1];
            $location.path(buildPage);
        }

        function goToViewDataPage(viewData) {
            $window.open(viewData, '_blank');
        }

        function openScheduledReport(scheduledObj) {
            $uibModal
                .open({
                    templateUrl:
                        "app/entities/flair-bi/scheduler/scheduler-dialog.html",
                    controller: "SchedulerDialogController",
                    controllerAs: "vm",
                    backdrop: "static",
                    size: "lg",
                    resolve: {
                        visualMetaData: function () {
                            return null;
                        },
                        datasource: function () {
                            return null;
                        },
                        view: function () {
                            return null;
                        },
                        dashboard: function () {
                            return null;
                        },
                        scheduledObj: function () {
                            return scheduledObj;
                        },
                        thresholdAlert: function () {
                            return scheduledObj.report.thresholdAlert;
                        }
                    }
                });
        }

        function updateReport(visualizationid) {
            $state.go('report-management');
            schedulerService.getScheduleReport(visualizationid).then(function (success) {
                if (success.status == 200) {
                    openScheduledReport(success.data.report);
                }
            }).catch(function (error) {
                var info = {
                    text: error.data.message,
                    title: "Error"
                }
                $rootScope.showErrorSingleToast(info);
            });
        }

        function executeNow(vizID) {
            schedulerService.executeNow(vizID).then(
                function (response) {
                    var info = {
                        text: 'Report executed',
                        title: 'Success'
                    };
                    $rootScope.showSuccessToast(info);
                },
                function (error) {
                    var info = {
                        text: 'A Webhook with this name already exists',
                        title: "Error"
                    };
                    $rootScope.showErrorSingleToast(info);
                });
        }
    }
})();
