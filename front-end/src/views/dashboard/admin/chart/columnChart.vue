<template>
  <div id="columnChart" />
</template>

<script>
import Highcharts from 'highcharts'
export default {
  data() {
    return {
      chart: null
    }
  },
  watch: {
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  methods: {
    initChart() {
      Highcharts.chart('columnChart', {
        chart: {
          type: 'column'
        },
        title: {
          text: ''
        },
        subtitle: {
          text: ''
        },
        xAxis: {
          categories: ['USA', 'China', 'Brazil', 'EU', 'India', 'Russia'],
          crosshair: true,
          accessibility: {
            description: 'Countries'
          }
        },
        yAxis: {
          min: 0,
          title: {
            text: null
          }
        },
        legend: {
          enabled: false
        },
        plotOptions: {
          column: {
            pointPadding: 0.2,
            borderWidth: 0
          }
        },
        series: [
          {
            name: 'Wheat',
            data: [51086, 136000, 5500, 141000, 107180, 77000]
          }
        ]
      })
    }
  }
}
</script>
<style scoped>
 #columnChart {
  height: 300px;
  margin-top: 10px;
 }
</style>
