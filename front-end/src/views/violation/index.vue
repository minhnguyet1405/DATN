<!-- eslint-disable vue/valid-v-model -->
<template>
  <div class="body">
    <div class="mb-4">
      <div id="vehicle-table">
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
          <el-table-column label="Họ và tên" width="220px">
            <template slot-scope="{row}">
              <div class="table-name">{{ getUserById(row.userId).fullName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="Phòng ban" width="400px">
            <template slot-scope="{row}">
              <div class="table-department">{{ getUserById(row.userId).department ? getUserById(row.userId).department.name : '' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="Số lượt vi phạm">
            <template slot-scope="{row}">
              <span class="table-number">{{ row.count }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Ngày vi phạm">
            <template slot-scope="{row}">
              <span class="table-number">{{ row.dates.join(';') }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import Cookies from 'js-cookie'
import user_default from '@/assets/images/user_default.png'
import { validEmail, validPhone } from '@/utils/validate'
import { inject } from 'vue'
import ImageCropper from '@/components/extra/ImageCropper'

export default {
  name: 'Accounts',
  components: {
  },
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  filters: {
    formatDatetime: function(value) {
      if (!value) return ''
      return moment(value, 'YYYY-MM-DD').format('DD/MM/YYYY')
    }
  },
  data() {
    return {
      loading: true,
      userList: [],
      topUserList: [],
      queryPage: {
        page: 0,
        size: 100,
        total: 0,
        search: ''
      },
      user: {
        uuid: '',
        username: '',
        password: '',
        matchingPassword: '',
        fullName: '',
        phoneNumber: '',
        email: '',
        gender: '',
        birthday: '',
        address: '',
        avatar: '',
        role: '',
        department: ''
      },
      departmentList: [],
      rolesLst: [
        {
          value: 1,
          label: 'Quản trị viên'
        },
        {
          value: 2,
          label: 'Nhân viên'
        }
      ]
    }
  },

  created() {
    this.getUser()
    this.getDepartment()
    this.getTopUser()
  },
  methods: {
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
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            search: this.queryPage.search
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
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
    getUserById(id) {
      return this.userList.find(i => i.uuid === id)
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
    }
  }
}
</script>

<style lang="scss">
.el-dialog {
  margin-top: 30px !important;
}

.item-left {
  width: 48%;
  float: left;
  margin-right: 2%;
}

.item-right {
  width: 48%;
  float: left;
  margin-left: 2%;
}

.block-item {
  width: 100%;
  float: left;
}

#select-all-delete .el-icon-close {
  font-size: 14px;
  margin-left: 18px;
  margin-right: 29px;
  top: 4px;
  cursor: pointer;
}

.el-icon-camera-solid {
  font-size: 25px;
  color: #9a9a9a;
  position: relative;
  top: -50px;
  left: 54px;
  background: #dfdfdf;
  border-radius: 50%;
  padding: 8px;
  border: 1px solid white;
}
img{
  width: 50px;
  border-radius: 50%;
}
</style>
