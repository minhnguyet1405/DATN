<template>
  <div id="pieChart" />
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
      Highcharts.chart('pieChart', {
        chart: {
          type: 'pie'
        },
        title: {
          text: ''
        },
        tooltip: {
          valueSuffix: '%'
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
              format: '{point.percentage:.1f}%',
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
            name: 'Percentage',
            colorByPoint: true,
            data: [
              {
                name: 'Water',
                y: 55.02
              },
              {
                name: 'Fat',
                sliced: true,
                selected: true,
                y: 26.71
              },
              {
                name: 'Carbohydrates',
                y: 1.09
              },
              {
                name: 'Protein',
                y: 15.5
              },
              {
                name: 'Ash',
                y: 1.68
              }
            ]
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
