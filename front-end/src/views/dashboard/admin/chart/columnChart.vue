<template>
  <div id="columnChart" />
</template>

<script>
import Highcharts from 'highcharts'
export default {
  props: {
    leaveList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    leaveList(val) {
      if (val) {
        this.$nextTick(() => {
          this.initChart(val)
        })
      }
    }
  },
  created() {
    console.log('leaveList', this.leaveList)
  },
  methods: {
    initChart(val) {
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
          categories: val.map(i => i.department),
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
            name: 'Số lượt nghỉ',
            data: val.map(i => i.number)
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
