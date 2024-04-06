<template>
  <div class="app-container">
    <div class="check-in-out-bock">
      <div class="filter">
        <el-date-picker
          v-model="filter.date"
          type="datetimerange"
          range-separator="-"
          start-placeholder="Từ ngày"
          end-placeholder="Đến ngày"
          format="dd/MM/yyyy HH:mm"
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
        :data="leaveList"
        style="width: 100%"
      >
        <el-table-column label="Nhân viên">
          <template slot-scope="scope"><span>{{ getNameUser(scope.row.createdBy) }}</span></template>
        </el-table-column>
        <el-table-column label="Từ ngày">
          <template slot-scope="scope"><span>{{ formatDate(scope.row.startTime) }}</span></template>
        </el-table-column>
        <el-table-column label="Đến ngày">
          <template slot-scope="scope"><span>{{ formatTime(scope.row.endTime) }}</span></template>
        </el-table-column>
        <el-table-column label="Trạng thái">
          <template slot-scope="scope"><span>{{ scope.row.status }}</span></template>
        </el-table-column>
        <el-table-column label="Lý do">
          <template slot-scope="scope"><span>{{ scope.row.reason }}</span></template>
        </el-table-column>
        <el-table-column label="Người duyệt">
          <template slot-scope="scope"><span>{{ getNameUser(scope.row.receive) }}</span></template>
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
        department: null,
        userId: null,
        date: [moment().subtract(1, 'month'), moment().add(1, 'month')]
      },
      leaveList: [],
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
    this.getList()
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
        .get(process.env.VUE_APP_API + 'management/leave', {
          headers: headers,
          params: {
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            userId: this.filter.userId,
            startTime: moment(this.filter.date[0]).format('YYYY-MM-DD HH:mm:ss'),
            endTime: moment(this.filter.date[1]).format('YYYY-MM-DD HH:mm:ss')
          }
        })
        .then((res) => {
          if (res.data) {
            this.leaveList = res.data.data
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

    getNameUser(id) {
      const user = this.userList.find(i => i.uuid === id)
      if (user) {
        return user.fullName
      } else {
        return ''
      }
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
  background-color: rgb(67, 35, 126);
  border-color: rgb(67, 35, 126);
}
.filter {
  display: flex;
  gap: 15px;
}
</style>
