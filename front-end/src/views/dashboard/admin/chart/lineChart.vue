<template>
  <div id="lightChart" />
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
      Highcharts.chart('lightChart', {
        title: {
          text: ''
        },
        xAxis: {
          tickInterval: 1,
          type: 'logarithmic',
          accessibility: {
            rangeDescription: 'Range: 1 to 10'
          }
        },
        legend: {
          enabled: false
        },

        yAxis: {
          title: {
            text: null
          },
          type: 'logarithmic',
          minorTickInterval: 0.1,
          accessibility: {
            rangeDescription: 'Range: 0.1 to 1000'
          }
        },
        series: [{
          data: [1, 2, 4, 8, 16, 32, 64, 128, 256, 512],
          pointStart: 1
        }]
      })
    }
  }
}
</script>
<style scoped>
 #lightChart {
  height: 300px;
  margin-top: 10px;
 }
</style>
