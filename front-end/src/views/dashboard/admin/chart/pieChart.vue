<template>
  <div id="pieChart" />
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
      chart: null,
      dataChart: [],
      leaveType: [
        { value: 'LEAVE', label: 'Nghỉ phép' },
        { value: 'SICK', label: 'Nghỉ ốm' },
        { value: 'ABSENT', label: 'Vắng mặt' }
      ]
    }
  },
  watch: {
    leaveList(val) {
      if (val) {
        this.dataChart = []
        val.forEach(i => {
          this.dataChart.push({
            name: this.leaveType.find(a => a.value === i.type).label,
            y: i.count
          })
        })
        this.$nextTick(() => {
          this.initChart()
        })
      }
    }
  },
  created() {
  },
  mounted() {
  },
  methods: {
    initChart() {
      Highcharts.chart('pieChart', {
        chart: {
          type: 'pie'
        },
        title: {
          text: ''
        },
        tooltip: {
          valueSuffix: ''
        },
        subtitle: {
          text: ''
        },
        plotOptions: {
          series: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: [{
              enabled: true,
              distance: 20
            }, {
              enabled: true,
              distance: -40,
              style: {
                fontSize: '1.2em',
                textOutline: 'none',
                opacity: 0.7
              },
              filter: {
                operator: '>',
                property: 'percentage',
                value: 10
              }
            }]
          }
        },
        series: [
          {
            name: 'Số lượng',
            colorByPoint: true,
            data: this.dataChart
          }
        ]
      })
    }
  }
}
</script>
<style scoped>
 #pieChart {
  height: 270px;
  margin-top: 10px;
 }
</style>
