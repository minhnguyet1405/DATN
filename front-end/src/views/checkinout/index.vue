<template>
  <div class="app-container">
    <div class="check-in-out-bock">
      <div>
        <el-date-picker
          v-model="filter.year"
          style="width: 100px"
          type="year"
          placeholder="Năm"
          @change="getList"
        />
        <el-date-picker
          v-model="filter.month"
          class="month"
          style="width: 120px; margin-left: 10px; height: 36px"
          format="MM"
          popper-class="custom-month"
          type="month"
          placeholder="Tháng"
          @change="getList"
        />
      </div>
      <div>
        <el-button
          class="btn-check"
          type="primary"
          :loading="loading_checkin"
          @click="handleCheckIn()"
        ><i class="el-icon-alarm-clock" />
          Chấm công
        </el-button>
      </div>
    </div>
    <div class="table-bock">
      <el-table
        ref="multipleTable"
        v-loading="loading"
        :data="checkinList"
        style="width: 100%"
      >
        <el-table-column label="Ngày">
          <template slot-scope="scope"><span>{{ formatDate(scope.row.date) }}</span></template>
        </el-table-column>
        <el-table-column label="In">
          <template slot-scope="scope"><span>{{ formatTime(scope.row.timeIn) }}</span></template>
        </el-table-column>
        <el-table-column label="Out">
          <template slot-scope="scope"><span>{{ scope.row.timeOut ? formatTime(scope.row.timeOut) :'--' }}</span></template>
        </el-table-column>
        <el-table-column label="Số phút đi muộn">
          <template slot-scope="scope"><span>{{ scope.row.timeLate }}</span></template>
        </el-table-column>
        <el-table-column label="Số phút về sớm">
          <template slot-scope="scope"><span>{{ scope.row.timeSoon }}</span></template>
        </el-table-column>
        <el-table-column label="Ghi chú">
          <template slot-scope="scope"><span>{{ scope.row.reason ? scope.row.reason :'--' }}</span></template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page.sync="queryPage.page"
        :page-sizes="[1, 5, 10, 15, 20]"
        :page-size="queryPage.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="queryPage.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      /></div>
  </div>
</template>
<script>
import axios from 'axios'
import { inject } from 'vue'
import Cookies from 'js-cookie'
import moment from 'moment'
export default {
  name: 'Accounts',
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  data() {
    return {
      loading: false,
      loading_checkin: false,
      filter: {
        year: moment().year().toString(),
        month: (moment().month() + 1).toString()
      },
      checkinList: [],
      queryPage: {
        page: 0,
        size: 10,
        total: 0
      }
    }
  },
  created() {
    console.log('aaaaaa', Cookies.getJSON('userInfo'))
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/check-in-out', {
          headers: headers,
          params: {
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            userId: Cookies.getJSON('userInfo').uuid,
            month: (moment(this.filter.month).month() + 1).toString(),
            year: (moment(this.filter.year).year()).toString()
          }
        })
        .then((res) => {
          if (res.data) {
            this.checkinList = res.data.data
            this.queryPage.total = res.total
            this.loading = false
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
    },
    handleCheckIn() {
      const params = {
        time: moment().format('YYYY-MM-DD HH:mm:ss'),
        userId: Cookies.getJSON('userInfo').uuid
      }
      const headers = {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      this.loading_checkin = true
      axios
        .post(
          process.env.VUE_APP_API + 'management/check-in-out',
          params,
          { headers }
        )
        .then((response) => {
          if (
            response.status === 200 ||
                response.status === 201
          ) {
            this.getList()
            this.$message.success('Chấm công thành công')
          }
          this.loading_checkin = false
        })
        .catch((err) => {
          this.loading_checkin = false
          this.$message({
            message: err.response.message,
            type: 'error'
          })
        })
    },
    handleSizeChange(size) {
      this.queryPage.size = size
      this.getList()
    },
    handleCurrentChange(page) {
      this.queryPage.page = page
      this.getList()
    },
    formatDate(val) {
      console.log(' 🚀 ~ formatDate ~ ', moment(val).format('DD/MM/YYYY'))
      return moment(val).format('DD/MM/YYYY')
    },
    formatTime(val) {
      return val ? moment(val).format('HH:mm') : ''
    }
  }
}
</script>
<style lang="scss">
.check-in-out-bock {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.btn-check {
  background-color: rgb(138, 63, 121);
  border-color: rgb(138, 63, 121);
}
</style>
