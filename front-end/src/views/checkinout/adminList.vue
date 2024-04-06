<template>
  <div class="app-container">
    <div class="check-in-out-bock">
      <div class="filter">
        <el-date-picker
          v-model="filter.year"
          style="width: 100px"
          type="year"
          placeholder="Năm"
        />
        <el-date-picker
          v-model="filter.month"
          class="month"
          style="width: 120px; margin-left: 10px; height: 36px"
          format="MM"
          popper-class="custom-month"
          type="month"
          placeholder="Tháng"
        />
        <el-select
          v-model="filter.department"
          style="width: 400px"
          filterable
          placeholder="Bộ phận"
          @change="getUserByDepartment"
        >
          <el-option
            v-for="department in departmentList"
            :key="department.id"
            :label="department.name"
            :value="department.id"
          />
        </el-select>
        <el-select
          v-model="filter.userId"
          style="width: 300px"
          filterable
          placeholder="Nhân viên"
          @change="handleSelectUser"
        >
          <el-option
            v-for="department in userList"
            :key="department.uuid"
            :label="department.fullName"
            :value="department.uuid"
          />
        </el-select>
        <div>
          <el-button
            class="btn-check"
            type="primary"
            :loading="loading"
            :disabled="disableBtn"
            @click="getList()"
          >
            Hiển thị dữ liệu
          </el-button>
        </div>
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
      disableBtn: true,
      loading: false,
      loading_checkin: false,
      filter: {
        department: null,
        userId: null,
        year: moment().year().toString(),
        month: (moment().month() + 1).toString()
      },
      checkinList: [],
      departmentList: [],
      userList: [],
      queryPage: {
        page: 0,
        size: 10,
        total: 0
      }
    }
  },
  created() {
    this.getDepartment()
    this.getUser()
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
            userId: this.filter.userId,
            month: (moment(this.filter.month).month() + 1).toString(),
            year: (moment(this.filter.year).year()).toString()
          }
        })
        .then((res) => {
          if (res.data) {
            this.checkinList = res.data.data
            this.queryPage.total = res.data.total
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
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            search: this.queryPage.search
          }
        })
        .then((res) => {
          if (res.data) {
            this.departmentList = res.data.data
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
    getUser() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user', {
          headers: headers,
          params: {
            page: 0,
            size: 1000
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
      this.filter.userId = null
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/by-department', {
          headers: headers,
          params: {
            department: this.filter.department
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
            console.log('this.userList', this.userList)
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
    },
    handleSelectUser() {
      if (this.filter.userId) {
        this.disableBtn = false
      } else {
        this.disableBtn = true
      }
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
  background-color: rgb(67, 35, 126);
  border-color: rgb(67, 35, 126);
}
.filter {
  display: flex;
  gap: 15px;
}
</style>
