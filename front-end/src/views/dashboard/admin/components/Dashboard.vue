<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
        <div class="card-panel-icon-wrapper icon-people">
          <!-- <svg-icon icon-class="peoples" class-name="card-panel-icon" /> -->
          <svg-icon icon-class="edit" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            Lượt vào
          </div>
          <count-to :start-val="0" :end-val="inputVal" :duration="300" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-message">
          <!-- <svg-icon icon-class="message" class-name="card-panel-icon" /> -->
          <svg-icon icon-class="link" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            Lượt ra
          </div>
          <count-to :start-val="0" :end-val="outputVal" :duration="300" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
        <div class="card-panel-icon-wrapper icon-shopping">
          <!-- <svg-icon icon-class="shopping" class-name="card-panel-icon" /> -->
          <svg-icon icon-class="education" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            Còn trống
          </div>
          <count-to :start-val="0" :end-val="position" :duration="300" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="money" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            Doanh thu
          </div>
          <count-to :start-val="0" :end-val="money" :duration="300" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'
import axios from 'axios'
import moment from 'moment'
import Cookies from 'js-cookie'

export default {
  components: {
    CountTo
  },
  data() {
    return {
      	startVal: 0,
      	inputVal: 0,
	    outputVal: 0,
	    position: 0,
	    money: 0,
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
      this.getInputTraffic()
      this.getTotalMoney()
      const self = this
	  const idIterval = setInterval(function() {
        self.getInputTraffic()
        self.getTotalMoney()
	  }, 30000)
      this.intervalId = idIterval
      window.localStorage.setItem('intervalId', idIterval)
    },

    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    },

    async getInputTraffic() {
      const headers = {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      const params = {
        startDate: moment(new Date()).format('YYYY-MM-DDT00:00:00'),
        endDate: moment(new Date()).format('YYYY-MM-DDT23:59:59'),
        objectType: [],
        eventType: ['IN'],
        sourceType: [],
        status: [
          'NOT_SEEN',
          'VERIFICATION',
          'PROCESSING',
          'PROCESSED'
        ]
      }
      // eslint-disable-next-line vue/no-async-in-computed-properties
      axios
        .post(process.env.VUE_APP_API + 'management', params, { headers })
        .then((res) => {
          if (res.data) {
            this.inputVal = res.data.total
            this.loading = false
            this.getOutputTraffic()
          }
        })
        .catch((err) => {
          console.log(moment(new Date()).format('YYYY-MM-DDT00:00:00'))
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },

    async getOutputTraffic() {
      const headers = {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      const params = {
        startDate: moment(new Date()).format('YYYY-MM-DDT00:00:00'),
        endDate: moment(new Date()).format('YYYY-MM-DDT23:59:59'),
        objectType: [],
        eventType: ['OUT'],
        sourceType: [],
        status: [
          'NOT_SEEN',
          'VERIFICATION',
          'PROCESSING',
          'PROCESSED'
        ]
      }
      // eslint-disable-next-line vue/no-async-in-computed-properties
      axios
        .post(process.env.VUE_APP_API + 'management', params, { headers })
        .then((res) => {
          if (res.data) {
            this.outputVal = res.data.total
            this.getPosition()
            this.loading = false
          }
        })
        .catch((err) => {
          console.log(moment(new Date()).format('YYYY-MM-DDT00:00:00'))
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },

    getPosition() {
      // cái này này chú
      this.position = 500 - this.inputVal + this.outputVal
    },

    getTotalMoney() {
      const headers = {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      // eslint-disable-next-line vue/no-async-in-computed-properties
      axios
        .get(process.env.VUE_APP_API + 'report/money', { headers })
        .then((res) => {
          if (res.data) {
            this.money = res.data.data
            this.loading = false
          }
        })
        .catch((err) => {
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    // box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
    border: 1px solid #e6ebf5;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #40c9c6;
      }

      .icon-message {
        background: #36a3f7;
      }

      .icon-money {
        background: #f4516c;
      }

      .icon-shopping {
        background: #34bfa3
      }
    }

    .icon-people {
      color: #40c9c6;
    }

    .icon-message {
      color: #36a3f7;
    }

    .icon-money {
      color: #f4516c;
    }

    .icon-shopping {
      color: #34bfa3
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
		float: right;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
