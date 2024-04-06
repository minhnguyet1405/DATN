<template>
  <div class="dashboard-editor-container">

    <panel-group @handleSetLineChartData="handleSetLineChartData" />

    <el-row
      style="background:#fff;padding:16px 16px 0;margin-bottom:32px;
      border: 1px solid #e6ebf5;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      height: 520px;"
    >
      <line-chart :chart-data="lineChartData" />
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'
import Cookies from 'js-cookie'
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'

const lineChartData = {
  expectedData: [100, 120, 161, 134, 105, 160, 165],
  actualData: [120, 82, 91, 154, 162, 140, 145]
}

export default {
  name: 'DashboardAdmin',
  components: {
    PanelGroup,
    LineChart
  },
  data() {
    return {
      lineChartData: lineChartData,
	  lineData: null,
	  intervalId: null
    }
  },
  created() {
    this.init()
  },
  beforeDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId)
    }
  },
  methods: {
    init() {
      this.getTrafficFlowReport()
	  const self = this
	  const idIterval = setInterval(function() {
        self.getTrafficFlowReport()
	  }, 30000)
	  this.intervalId = idIterval
      window.localStorage.setItem('intervalId', idIterval)
    },
    handleSetLineChartData(type) {
      this.lineChartData = lineChartData[type]
    },
    async getTrafficFlowReport() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      await axios
        .get(process.env.VUE_APP_API + 'report/line', {
          headers: headers
        })
        .then((res) => {
          if (res.data) {
            this.lineData = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
      if (this.lineData) {
        this.lineChartData.actualData = []
        this.lineChartData.expectedData = []
        const day = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ nhật']
        day.forEach((d) => {
          let flag = false
          this.lineData.forEach((element) => {
            if (d == element.time) {
              flag = true
              if (element.code == 'Vào') {
                this.lineChartData.actualData.push(element.total)
              } else {
                this.lineChartData.expectedData.push(element.total)
			  }
            }
          })
		  if (flag == false) {
            this.lineChartData.actualData.push(0)
            this.lineChartData.expectedData.push(0)
          }
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  padding-top: 15px;
  // background-color: rgb(240, 242, 245);
  position: relative;
  background-color: #FFFFFF;

  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
