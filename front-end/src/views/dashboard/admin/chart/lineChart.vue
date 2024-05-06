<template>
  <div id="lightChart" />
</template>

<script>
import Highcharts from 'highcharts'
import moment from 'moment'
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
      allDays: []
    }
  },
  watch: {
    leaveList(val) {
      this.getAllDaysInMonth()
      if (val) {
        const newArray = []
        for (let i = 0; i < this.allDays.length; i++) {
          for (let j = 0; j < val.length; j++) {
            if (this.allDays[i] === val[j].day) {
              newArray.push({ day: this.allDays[i], value: val[j].number })
            } else {
              newArray.push({ day: this.allDays[i], value: 0 })
            }
          }
        }
        this.$nextTick(() => {
          this.initChart(newArray)
        })
      }
    }
  },
  created() {
    this.getAllDaysInMonth()
  },
  methods: {
    getAllDaysInMonth() {
      const firstDayOfMonth = moment().startOf('month')
      const lastDayOfMonth = moment().endOf('month')
      const allDays = []
      const currentDay = firstDayOfMonth
      while (currentDay.isSameOrBefore(lastDayOfMonth, 'day')) {
        allDays.push(moment(currentDay.clone()).format('YYYY-MM-DD'))
        currentDay.add(1, 'day')
      }
      this.allDays = allDays
    },
    initChart(newArray) {
      Highcharts.chart('lightChart', {
        chart: {
          type: 'line'
        },
        title: {
          text: ''
        },
        subtitle: {
          text: ''
        },
        xAxis: {
          categories: newArray.map(i => i.day)
        },
        yAxis: {
          title: {
            text: 'Số lần'
          }
        },
        plotOptions: {
          line: {
            dataLabels: {
              enabled: false
            },
            enableMouseTracking: false
          }
        },
        series: [{
          name: 'Số lượt nghỉ',
          data: newArray.map(i => i.value)
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
