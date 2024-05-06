<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
  <div class="dashboard-container">
    <div class="db-header">
      <div class="dashboard-title">Tổng quan</div>
      <div class="db-header-filter">
        <div>
          <el-select v-model="departmentId" placeholder="Tất cả đơn vị" @change="getUserByDepartment">
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </div>
        <div class="filter-config">
          <i class="el-icon-s-tools" />
          Tùy chỉnh
        </div>
      </div>
    </div>
    <div class="db-body">
      <div class="db-left">
        <div class="db-left-item">
          <div class="db-left-item-title">Đi muộn, về sớm</div>
          <el-select v-model="time1" placeholder="Select" @change="getCountTimeLateSoon">
            <el-option
              v-for="item in optionTimes1"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="db-left-item-number">
            <div>{{ countTimeLate }}</div>
          </div>
          <div class="db-left-item-detail">
            Chi tiết
          </div>
        </div>
        <div class="db-left-item">
          <div class="db-left-item-title">Thực tế đã nghỉ</div>
          <el-select v-model="time2" placeholder="Select" @change="getRealLeave">
            <el-option
              v-for="item in optionTimes1"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="db-left-item-number">
            <div>{{ leaveReal }}</div>
          </div>
          <div class="db-left-item-detail">
            Chi tiết
          </div>
        </div>
        <div class="db-left-item">
          <div class="db-left-item-title">Kế hoạch nghỉ</div>
          <el-select v-model="time3" placeholder="Select" @change="getLeavePlan">
            <el-option
              v-for="item in optionTimes1"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="db-left-item-number">
            <div>{{ leavePlan }}</div>
          </div>
          <div class="db-left-item-detail">
            Chi tiết
          </div>
        </div>
      </div>
      <div class="db-right">
        <div class="db-right-top">
          <div class="db-right-top-item">
            <div class="leave-time-header">
              <div>
                <div class="leave-time-header-title">Tình hình nghỉ theo thời gian</div>
                <div class="leave-time-header-content">{{ lableDepartment }}</div>
                <div class="leave-time-header-content">{{ getLabelTime }}</div>
              </div>
              <div class="leave-time-icon">
                <i class="el-icon-refresh-left" />
                <i class="el-icon-download" />
                <i class="el-icon-setting" />
              </div>
            </div>
            <LineChart :leave-list="leaveByTimeList" />
          </div>
          <div class="db-right-top-item">
            <div class="leave-time-header">
              <div>
                <div class="leave-time-header-title">Tình hình nghỉ theo phòng ban</div>
                <div class="leave-time-header-content-2">Công ty cổ phần công nghệ viễn thông Elcom</div>
                <div class="leave-time-header-content-2">(01/11/2021 - 05/12/2021)</div>
              </div>
              <div class="leave-time-icon" />
            </div>
            <ColumnChart :leave-list="leaveByDepartmentList" />
          </div>
        </div>
        <div class="db-right-bottom">
          <div class="db-right-bottom-item">
            <div class="leave-time-header">
              <div>
                <div class="leave-time-header-title">Phân tích loại nghỉ</div>
                <div class="leave-time-header-content-2">Công ty cổ phần công nghệ viễn thông Elcom</div>
                <div class="leave-time-header-content-2">{{ getLabelTime }}</div>
              </div>
            </div>
            <PieChart :leave-list="leaveByTypeList" />
          </div>
          <div class="db-right-bottom-item">
            <div>
              <div class="leave-time-header-title">Danh sách đi muộn về sớm</div>
              <div class="leave-time-header-content-2">Công ty cổ phần công nghệ viễn thông Elcom</div>
              <div class="leave-time-header-content-2">{{ getLabelTime }}</div>
            </div>
            <el-table
              class="table1"
              :data="topUserList"
              style="width: 100%"
            >
              <el-table-column label="" width="70px">
                <template slot-scope="{row}">
                  <img :src="getUserById(row.userId).avatar">
                </template>
              </el-table-column>
              <el-table-column label="" width="220px">
                <template slot-scope="{row}">
                  <div class="table-name">{{ getUserById(row.userId).fullName }}</div>
                  <div class="table-department">{{ getUserById(row.userId).department ? getUserById(row.userId).department.name : '' }}</div>
                </template>
              </el-table-column>
              <el-table-column label="">
                <template slot-scope="{row}">
                  <span class="table-number">{{ row.count }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="db-right-bottom-item">
            <div>
              <div class="leave-time-header-title">Tần suất đi muộn về sớm</div>
              <div class="leave-time-header-content-2">{{ lableDepartment }}}</div>
              <div class="leave-time-header-content-2">{{ getLabelTime }}</div>
            </div>
            <el-table
              class="table2"
              :data="topfrequencyList"
              style="width: 100%"
            >
              <el-table-column label="" width="250px">
                <template slot-scope="{row}">
                  <span class="table-name">{{ row.frequency }}</span>
                </template>
              </el-table-column>
              <el-table-column label="">
                <template slot-scope="{row}">
                  <span class="table-number-2">{{ row.count }} lần</span>
                  <i class="el-icon-caret-right" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>

import LineChart from './chart/lineChart.vue'
import ColumnChart from './chart/columnChart.vue'
import PieChart from './chart/pieChart.vue'
import axios from 'axios'
import Cookies from 'js-cookie'
import moment from 'moment'

export default {
  components: {
    LineChart,
    ColumnChart,
    PieChart
  },
  data() {
    return {
      lableDepartment: 'Tất cả các đơn vị',
      departmentId: null,
      userList: [],
      departmentList: [],
      topUserList: [],
      topfrequencyList: [],
      leaveByTimeList: [],
      leaveByDepartmentList: [],
      time1: 'today',
      time2: 'today',
      time3: 'today',
      countTimeLate: 0,
      leaveReal: 0,
      leavePlan: 0,
      leaveByTypeList: [],
      optionTimes1: [
        { value: 'today', label: 'Hôm nay' },
        { value: 'this_week', label: 'Tuàn này' },
        { value: 'this_month', label: 'Tháng này' }
      ],
      tableData1: [
        { imageUrl: 'http://103.21.151.166:8683/v1.0/upload/user/avatar/05032024/b194f8d9-ad53-4f87-a93a-82c5bc61b12a.png', name: 'Phạm Minh Thắng', department: 'Phòng ITS', number: 40 }
      ],
      tableData2: [
        { title: 'Trên 6 lần', number: 2 },
        { title: '6 lần', number: 1 },
        { title: '5 lần', number: 3 }
      ]
    }
  },
  computed: {
    getLabelTime() {
      const startOfMonth = moment().startOf('month')
      const endOfmonth = moment().endOf('month')
      const startTime = moment(startOfMonth).format('DD/MM/YYYY')
      const endTime = moment(endOfmonth).format('DD/MM/YYYY')
      return startTime + ' - ' + endTime
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
    async init() {
      await this.getUser()
      await this.getDepartment()
      await this.getCountTimeLate()
      this.getRealLeave()
      this.getLeavePlan()
      this.getLeaveByType()
      this.getTopUser()
      this.getTopfrequency()
      this.getLeaveByTime()
      this.getLeaveByDepartment()
    },
    getDepartment() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/department', {
          headers: headers,
          params: {
            page: 0,
            size: 100
          }
        })
        .then((res) => {
          if (res.data) {
            this.departmentList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getUser() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user', {
          headers: headers,
          params: {
            page: 0,
            size: 100
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getUserByDepartment() {
      this.lableDepartment = this.departmentList.find(i => i.id === this.departmentId).name
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/by-department', {
          headers: headers,
          params: {
            department: this.departmentId
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
            this.init()
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getCountTimeLate() {
      const param = {
        startTime: null,
        endTime: null,
        userIds: null
      }
      if (this.time1 === 'today') {
        param.startTime = moment().format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      } else if (this.time1 === 'this_week') {
        const startOfWeek = moment().startOf('week')
        param.startTime = moment(startOfWeek).format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      } else {
        const startOfMonth = moment().startOf('month')
        param.startTime = moment(startOfMonth).format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      }
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/time-late', {
          headers: headers,
          params: {
            startTime: param.startTime,
            endTime: param.endTime,
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.countTimeLate = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getRealLeave() {
      const param = {
        startTime: null,
        endTime: null,
        userIds: null
      }
      if (this.time2 === 'today') {
        param.startTime = moment().format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      } else if (this.time2 === 'this_week') {
        const startOfWeek = moment().startOf('week')
        param.startTime = moment(startOfWeek).format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      } else {
        const startOfMonth = moment().startOf('month')
        param.startTime = moment(startOfMonth).format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      }
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave-real', {
          headers: headers,
          params: {
            startTime: param.startTime,
            endTime: param.endTime,
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.leaveReal = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getLeavePlan() {
      const param = {
        startTime: null,
        endTime: null,
        userIds: null
      }
      if (this.time3 === 'today') {
        param.startTime = moment().format('YYYY-MM-DD 00:00:00')
        param.endTime = moment().format('YYYY-MM-DD 23:59:59')
      } else if (this.time3 === 'this_week') {
        const startOfWeek = moment().startOf('week')
        const endOfWeek = moment().endOf('week')
        param.startTime = moment(startOfWeek).format('YYYY-MM-DD 00:00:00')
        param.endTime = moment(endOfWeek).format('YYYY-MM-DD 23:59:59')
      } else {
        const startOfMonth = moment().startOf('month')
        const endOfmonth = moment().endOf('month')
        param.startTime = moment(startOfMonth).format('YYYY-MM-DD 00:00:00')
        param.endTime = moment(endOfmonth).format('YYYY-MM-DD 23:59:59')
      }
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave-plan', {
          headers: headers,
          params: {
            startTime: param.startTime,
            endTime: param.endTime,
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.leavePlan = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getLeaveByType() {
      this.leaveByTypeList = []
      const param = {
        startTime: null,
        endTime: null,
        userIds: null
      }
      const startOfMonth = moment().startOf('month')
      param.startTime = moment(startOfMonth).format('YYYY-MM-DD 00:00:00')
      param.endTime = moment().format('YYYY-MM-DD 23:59:59')

      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave-by-type', {
          headers: headers,
          params: {
            startTime: param.startTime,
            endTime: param.endTime,
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.leaveByTypeList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getTopUser() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/top-user-late-soon', {
          headers: headers,
          params: {
            month: moment().month() + 1,
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.topUserList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getTopfrequency() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/frequency-late-soon', {
          headers: headers,
          params: {
            month: moment().month() + 1,
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.topfrequencyList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getLeaveByTime() {
      this.leaveByTimeList = []
      const startOfMonth = moment().startOf('month')
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave-by-time', {
          headers: headers,
          params: {
            startTime: moment(startOfMonth).format('YYYY-MM-DD 00:00:00'),
            endTime: moment().format('YYYY-MM-DD 23:59:59'),
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.leaveByTimeList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getLeaveByDepartment() {
      this.leaveByDepartmentList = []
      const startOfMonth = moment().startOf('month')
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave-by-department', {
          headers: headers,
          params: {
            startTime: moment(startOfMonth).format('YYYY-MM-DD 00:00:00'),
            endTime: moment().format('YYYY-MM-DD 23:59:59'),
            userIds: this.userList.length > 0 ? this.userList.map(u => u.uuid).join(',') : null
          }
        })
        .then((res) => {
          if (res.data) {
            this.leaveByDepartmentList = res.data.data
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getCountTimeLateSoon() {
      this.getCountTimeLate()
      this.getCountTimeSoon()
    },
    getUserById(id) {
      return this.userList.find(i => i.uuid === id)
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-title {
  font-size: 18px;
  font-weight: 600;
}
.dashboard-container {
  background: rgb(235, 233, 233);
}
.dashboard-container {
  padding: 10px 15px;
}
.db-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.db-header-filter {
  height: 60px;
  display: flex;
  gap: 10px;
}
.filter-config {
  font-weight: 600;
  border-radius: 3px;
  height: 35px;
  display: flex;
  gap: 5px;
  align-items: center;
  padding: 0 15px;
  background: white;
  i {
    color: rgb(82, 80, 80);
  }
}
.db-body {
  display: flex;
  gap: 15px
}
.db-left {}
.db-left-item {
  padding: 15px;
  width: 250px;
  margin-bottom: 15px;
  height: 250px;
  background-color: white;

  .db-left-item-title {
    font-weight: 600;
  }

  .db-left-item-number {
    width: 100%;
    height: 150px;
    display: flex;
    div {
      font-size: 44px;
      font-weight: 600;
      margin: auto;
    }
  }
  .db-left-item-detail {
    display: flex;
    justify-content: end;
    color: rgb(241, 107, 107);
    font-size: 16px;
  }

  ::v-deep .el-select {
    input {
      padding: 0;
      width: 90px;
      border: none;
    }
  }
}
.db-right {
  width: 100%;
}
.db-right-top {
  display: flex;
  gap: 15px;
}
.db-right-top-item {
  background: white;
  flex: 1;
  margin-bottom: 15px ;
  height: 400px;
}
.db-right-bottom {
  display: flex;
  gap: 15px;
}
.db-right-bottom-item {
  background: white;
  flex: 1;
  height: 365px;
}

.leave-time-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.leave-time-header-title{
  font-size: 16px;
  font-weight: 600;
}
.leave-time-header-content{
  font-size: 14px;
  color: rgb(241, 107, 107);
  margin-top: 5px;
}
.leave-time-header-content-2{
  font-size: 14px;
  color: rgb(95, 84, 84);
  margin-top: 5px;
}
.leave-time-icon {
  display: flex;
  align-items: center;
  gap: 10px;
  i {
    font-size: 20px;
    font-weight: 600;
    color: rgb(100, 102, 102);
  }

}
.db-right-top-item {
  padding: 15px;
}

.db-right-bottom-item {
  padding: 15px;
}
.table1 {
  img {
    width: 50px;
    border-radius: 50%;
  }
}
.table-department{
  font-size: 14px;
  color: #797676;
}
.table-name{
  font-size: 16px;
  color: black;
}
.table-number {
  font-size: 18px;
  font-weight: 600;
}
.table-number-2 {
  font-size: 16px;
}
</style>
