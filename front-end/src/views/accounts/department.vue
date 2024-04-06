<!-- eslint-disable vue/valid-v-model -->
<template>
  <div class="body">
    <div>
      <el-input
        v-model="queryPage.search"
        class="input-search"
        placeholder="Tìm kiếm"
        prefix-icon="el-icon-search"
        @change="onChangeInputSearch"
      />
      <el-button
        class="btn-add"
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate()"
      >Thêm mới</el-button>
    </div>
    <div class="mb-4">
      <div id="vehicle-table">
        <!-- <div class="table-responsive"> -->
        <div v-if="multiSelected.length > 0" id="select-all-delete">
          <i class="el-icon-close" @click="closeDelete()" />
          <span
            v-if="multiSelected.length < 10"
          >0{{ multiSelected.length }}</span><span v-else>{{ multipleSelection.length }}</span> mục được chọn
          <el-button
            type="danger"
            :loading="loading_delete_all"
            @click="destroyMulti()"
          ><i class="el-icon-delete" /> Xóa</el-button>
        </div>
        <el-table
          ref="multipleTable"
          v-loading="loading"
          :data="departmentList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @row-click="rowClicked"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column label="STT" width="50">
            <template slot-scope="scope">{{
              scope.$index +
                1 +
                (queryPage.page > 0 ? queryPage.page - 1 : 0) * queryPage.size
            }}</template>
          </el-table-column>
          <el-table-column prop="name" label="Tên phòng ban" />
          <el-table-column prop="code" label="Mã phòng ban" />
          <el-table-column prop="site" label="Vị trí" />
          <el-table-column prop="phoneNumber" label="Số điện thoại" />
        </el-table>
        <el-pagination
          :current-page.sync="queryPage.page"
          :page-sizes="[1, 5, 10, 15, 20]"
          :page-size="queryPage.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="queryPage.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      <div id="detail-vehicle" class="violation-ruleForm" style="width: 0">
        <i class="el-icon-close" @click="closeDetail()" />
        <template v-if="departmentDetail">

          <template v-if="departmentDetail.name">
            <div class="item-label">Tên phòng ban</div>
            <div class="item-text">{{ departmentDetail.name }}</div>
          </template>

          <template v-if="departmentDetail.code">
            <div class="item-label">Mã phòng ban</div>
            <div class="item-text">
              {{ departmentDetail.code }}
            </div>
          </template>

          <template v-if="departmentDetail.site">
            <div class="item-label">Vị trí</div>
            <div class="item-text">
              {{ departmentDetail.site }}
            </div>
          </template>

          <template v-if="departmentDetail.phoneNumber">
            <div class="item-label">Số điện thoại</div>
            <div class="item-text">{{ departmentDetail.phoneNumber }}</div>
          </template>

          <template>
            <div class="item-label">Trưởng phòng</div>
            <div class="item-text">{{ departmentDetail.managerId }}</div>
          </template>

          <template>
            <div class="item-label">Số lượng vân viên</div>
            <div class="item-text">{{ departmentDetail.numberStaff }}</div>
          </template>

          <div class="action">
            <el-button
              type="info"
              @click="handleEdit(departmentDetail)"
            ><i class="el-icon-edit" /> Sửa
            </el-button>
            <el-button
              type="danger"
              style="float: right"
              :loading="loadingVehicle"
              @click="destroy(departmentDetail)"
            ><i class="el-icon-delete" /> Xóa
            </el-button>
          </div>
        </template>
      </div>
    </div>

    <el-dialog
      title="Thêm mới"
      :visible.sync="dialogAdd"
      width="700px"
      :close-on-click-modal="false"
      @close="closeDialog('addForm')"
    >
      <el-form
        ref="addForm"
        :model="departmentInfo"
        :rules="rules"
        label-position="top"
        label-width="100%"
      >
        <div class="block-item">
          <div class="item-left">
            <el-form-item
              style="margin-bottom: 21px"
              label="Tên phòng ban"
              prop="name"
            >
              <el-input
                v-model="departmentInfo.name"
                placeholder="Nhập tên"
              />
            </el-form-item>

            <el-form-item
              style="margin-bottom: 21px"
              label="Mã phòng ban"
              prop="code"
            >
              <el-input
                v-model="departmentInfo.code"
                placeholder="Nhập mã"
              />
            </el-form-item>
          </div>
          <div class="item-right">
            <el-form-item
              style="margin-bottom: 21px"
              label="Số điện thoại"
              prop="phoneNumber"
            >
              <el-input
                v-model="departmentInfo.phoneNumber"
                placeholder="Nhập số điện thoại"
              />
            </el-form-item>

            <el-form-item
              style="margin-bottom: 21px"
              label="Vị trí"
              prop="site"
            >
              <el-input v-model="departmentInfo.site" placeholder="Nhập vị trí" />
            </el-form-item>
          </div>
        </div>
      </el-form>

      <div slot="footer">
        <el-button
          class="cancel-btn"
          type="info"
          @click="dialogAdd = false"
        >Hủy</el-button>
        <el-button
          type="primary"
          :loading="loading_add"
          @click="addDepartment()"
        >Lưu
        </el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="Cập nhật"
      :visible.sync="dialogEdit"
      width="700px"
      :close-on-click-modal="false"
      @close="closeDialog('editForm')"
    >
      <el-form
        ref="editForm"
        :model="departmentInfo"
        :rules="rulesEdit"
        label-position="top"
        label-width="100%"
      >
        <div class="block-item">
          <div class="item-left">
            <el-form-item
              style="margin-bottom: 21px"
              label="Tên phòng ban"
              prop="name"
            >
              <el-input
                v-model="departmentInfo.name"
                placeholder="Nhập tên"
              />
            </el-form-item>

            <el-form-item
              style="margin-bottom: 21px"
              label="Mã phòng ban"
              prop="code"
            >
              <el-input
                v-model="departmentInfo.code"
                placeholder="Nhập mã"
              />
            </el-form-item>
          </div>
          <div class="item-right">
            <el-form-item
              style="margin-bottom: 21px"
              label="Số điện thoại"
              prop="phoneNumber"
            >
              <el-input
                v-model="departmentInfo.phoneNumber"
                placeholder="Nhập số điện thoại"
              />
            </el-form-item>

            <el-form-item
              style="margin-bottom: 21px"
              label="Vị trí"
              prop="site"
            >
              <el-input v-model="departmentInfo.site" placeholder="Nhập vị trí" />
            </el-form-item>
          </div>
        </div>
      </el-form>

      <div slot="footer">
        <el-button
          class="cancel-btn"
          type="info"
          @click="dialogEdit = false"
        >Hủy</el-button>
        <el-button
          type="primary"
          :loading="loading_add"
          @click="editUser()"
        >Lưu
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import Cookies from 'js-cookie'
import user_default from '@/assets/images/user_default.png'
import { validPhone } from '@/utils/validate'
import { inject } from 'vue'

export default {
  name: 'Accounts',
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
    const validateMobile = (rule, value, callback) => {
      if (value && !validPhone(value)) {
        callback(new Error('Vui lòng nhập đúng định dạng số điện thoại'))
      } else {
        callback()
      }
    }

    return {
      loading: true,
      loadingVehicle: false,
      departmentList: [],
      multiSelected: [],
      allSelected: false,
      loading_delete_all: false,
      loading_add: false,
      dialogEdit: false,
      dialogAdd: false,
      queryPage: {
        page: 0,
        size: 10,
        total: 0,
        search: ''
      },
      user: {
        id: '',
        name: '',
        code: '',
        site: '',
        phoneNumber: ''
      },
      departmentInfo: {
        id: '',
        name: '',
        code: '',
        site: '',
        phoneNumber: ''
      },
      userUpdate: {
        id: '',
        name: '',
        code: '',
        site: '',
        phoneNumber: ''
      },
      departmentDetail: {},
      imagecropperKey: 0,
      imagecropperShow: false,
      url: null,
      user_default: user_default,
      uploadReady: true,
      datePickerOptions: {
        disabledDate(date) {
          return date > moment().valueOf()
        }
      },
      file: {
        data: null
      },
      avatarHeight: 200,
      avatarWith: 200,
      rolesLst: [
        {
          value: 1,
          label: 'Quản trị viên'
        },
        {
          value: 2,
          label: 'Nhân viên'
        }
      ],
      rules: {
        name: [
          {
            required: true,
            message: 'Tên phòng ban là bắt buộc',
            trigger: 'blur'
          }
          //   { validator: vehicleType }
        ],
        code: [
          {
            required: true,
            message: 'Mã phòng ban là bắt buộc',
            trigger: 'blur'
          },
          { max: 50, message: 'Tối đa 50 ký tự', trigger: 'blur' }
        ],
        site: [
          {
            required: true,
            message: 'Vị trí là bắt buộc',
            trigger: 'blur'
          }
        ],
        phoneNumber: [
          {
            required: true,
            message: 'Số điện thoại là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateMobile }
        ]
      },
      rulesEdit: {
        name: [
          {
            required: true,
            message: 'Tên phòng ban là bắt buộc',
            trigger: 'blur'
          }
          //   { validator: vehicleType }
        ],
        code: [
          {
            required: true,
            message: 'Mã phòng ban là bắt buộc',
            trigger: 'blur'
          },
          { max: 50, message: 'Tối đa 50 ký tự', trigger: 'blur' }
        ],
        site: [
          {
            required: true,
            message: 'Vị trí là bắt buộc',
            trigger: 'blur'
          }
        ],
        phoneNumber: [
          {
            required: true,
            message: 'Số điện thoại là bắt buộc',
            trigger: 'blur'
          },
          { validator: validateMobile }
        ]
      }
    }
  },
  watch: {
    multiSelected(newValue) {
      if (newValue.length === 0) {
        this.indeterminate = false
        this.allSelected = false
      } else if (newValue.length === this.departmentList.length) {
        this.indeterminate = false
        this.allSelected = true
      } else {
        this.indeterminate = true
        this.allSelected = false
      }
    }
  },
  created() {
    this.getDepartment()
  },
  methods: {
    closeDialog(formName) {
      this.$refs[formName].clearValidate()
      this.$refs[formName].resetFields()
    },

    resetDialog() {
      this.url = null
      this.departmentInfo = {
        uuid: '',
        email: '',
        phoneNumber: '',
        username: '',
        fullName: '',
        matchingPassword: '',
        password: '',
        gender: '',
        birthday: '',
        address: '',
        avatar: '',
        role: ''
      }
    },

    toggleAll(checked) {
      this.multiSelected = checked ? this.departmentList.slice() : []
    },
    handleSizeChange(size) {
      this.queryPage.size = size
      this.getDepartment()
    },
    handleCurrentChange(page) {
      this.queryPage.page = page
      this.getDepartment()
    },
    onChangeInputSearch() {
      this.queryPage.page = 0
      this.getDepartment()
    },
    handleSelectionChange(val) {
      this.multiSelected = val
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

    closeDelete() {
      this.$refs.multipleTable.clearSelection()
      this.multipleSelection = []
    },

    destroyMulti() {
      this.$confirm('Bạn có chắc chắn muốn xóa lựa chọn này?', '', {
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Thoát',
        type: 'warning'
      })
        .then(() => {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loading_delete_all = true
          let params = this.multiSelected.map(function(val) {
            return val.id
          })
          params = {
            ids: params
          }
          axios
            .delete(process.env.VUE_APP_API + 'user/department', {
              headers: headers,
              data: params
            })
            .then((res) => {
              this.multiSelected = []
              this.onChangeInputSearch()
              this.loading_delete_all = false
              this.$message({
                type: 'success',
                message: res.data.message
              })
            })
            .catch((err) => {
              console.log(err)
              this.multiSelected = []
              this.onChangeInputSearch()
              this.loading_delete_all = false

              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        })
        .catch(() => {})
    },

    async rowClicked(row) {
      this.flag_active = row.uuid
      if (row) {
        this.departmentDetail = row
        if (this.departmentDetail) {
          document.getElementById('vehicle-table').style.width =
            'calc(100% - 406px)'
          setTimeout(function() {
            document.getElementById('detail-vehicle').style.display = 'block'
            document.getElementById('detail-vehicle').style.width = '380px'
          }, 500)
        }
      } else {
        this.$message({
          message: 'Không tìm thấy phương tiện',
          type: 'error'
        })
        this.onChangeInputSearch()
      }
    },

    closeDetail() {
      document.getElementById('detail-vehicle').style.display = 'none'
      document.getElementById('detail-vehicle').style.width = '0'
      document.getElementById('vehicle-table').style.width = 'calc(100%)'
    },

    destroy(vehicle) {
      this.$confirm('Bạn có chắc chắn muốn xóa lựa chọn này?', '', {
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Thoát',
        type: 'warning'
      })
        .then(() => {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loadingVehicle = true
          let params = this.multiSelected.map(function(val) {
            return val.uuid
          })
          params.push(vehicle.uuid)
          params = {
            uuids: params
          }
          axios
            .delete(process.env.VUE_APP_API + 'user', {
              headers: headers,
              data: params
            })
            .then((res) => {
              console.log(res)
              this.multiSelected = []
              this.onChangeInputSearch()
              this.closeDetail()
              this.loadingVehicle = false
              this.$message({
                type: 'success',
                message: res.data.message
              })
            })
            .catch((err) => {
              console.log(err)
              this.multiSelected = []
              this.onChangeInputSearch()
              this.loadingVehicle = false

              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        })
        .catch(() => {})
    },

    handleCreate() {
      this.resetDialog()
      this.dialogAdd = true
      this.departmentInfo.gender = 0
    },
    handleEdit(data) {
      this.departmentInfo = _.cloneDeep(data)
      this.url = data.avatar
      this.dialogEdit = true
      this.$nextTick(() => {
        this.$refs['editForm'].clearValidate()
      })
    },

    editUser() {
      this.departmentInfo = this.$root.trimData(this.departmentInfo)
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          const params = {
            id: this.departmentInfo.id,
            name: this.departmentInfo.name,
            phoneNumber: this.departmentInfo.phoneNumber.trim(),
            code: this.departmentInfo.code,
            site: this.departmentInfo.site
          }
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loadingVehicle = true
          axios
            .put(
              process.env.VUE_APP_API + 'user/department',
              params,
              { headers }
            )
            .then((response) => {
              if (
                response.data.status === 200 ||
                response.data.status === 201
              ) {
                this.dialogEdit = false
                this.closeDetail()
                this.getDepartment()
                this.$message({
                  message: response.data.message,
                  type: 'success'
                })
                this.resetDialog()
              } else {
                this.dialogEdit = false
                this.getDepartment()
                this.$message({
                  message: response.data.message,
                  type: 'error'
                })
              }
              this.loadingVehicle = false
              this.resetDialog()
            })
            .catch((err) => {
              this.loadingVehicle = false
              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        } else {
          return false
        }
      })
    },
    resetUploadFile() {
      this.uploadReady = false
      this.$nextTick(() => {
        this.uploadReady = true
      })
    },

    cropSuccess(data) {
      if (data.data[0].fileDownloadUri) this.url = data.data[0].fileDownloadUri
    },
    fileSelected(e) {
      const file = e.target.files[0]
      if (file) {
        this.checkFile(file)
          .then(() => {
            this.file.data = file
            this.imagecropperShow = true
            this.resetUploadFile()
          })
          .catch((err) => {
            this.$message({
              message: err,
              type: 'error',
              duration: 5 * 1000,
              showClose: true
            })
          })
      }
    },
    checkFile(file) {
      return new Promise((resolve, reject) => {
        const self = this
        if (file.type.indexOf('image') === -1) {
          return reject('Định dạng không cho phép')
        }
        if (file.size > 5242880) {
          return reject('Vượt quá dung lượng cho phép tối đa 5Mb')
        }
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = function(e) {
          const image = new Image()
          image.src = e.target.result
          image.onload = function() {
            const height = this.height
            const width = this.width
            if (height < self.avatarHeight || width < self.avatarWith) {
              return reject(
                'Kích thước hình ảnh quá nhỏ. Tối thiểu là: ' +
                  self.avatarWith +
                  '*' +
                  self.avatarHeight
              )
            }
            return resolve()
          }
        }
      })
    },
    closeChangeAvatar() {},
    handleClick(e) {
      if (e.target !== this.$refs.file) {
        e.preventDefault()
        if (document.activeElement !== this.$refs) {
          this.$refs.file.click()
        }
      }
    },

    addDepartment() {
      this.departmentInfo = this.$root.trimData(this.departmentInfo)
      this.$refs.addForm.validate((valid) => {
        if (valid) {
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          const data = _.cloneDeep(this.departmentInfo)
          // const paramRegister = this.departmentInfo
          const paramRegister = {
            name: data.name,
            code: data.code,
            site: data.site,
            phoneNumber: data.phoneNumber
          }
          this.loading_add = true
          axios
            .post(process.env.VUE_APP_API + 'user/department', paramRegister, { headers })
            .then((response) => {
              console.log(response.data)
              if (
                response.data.status === 200 ||
                response.data.status === 201
              ) {
                this.$message({
                  type: 'success',
                  message: response.data.message
                })
                this.dialogAdd = false
                this.getDepartment()
                this.loading_add = false
                this.resetDialog()
              } else {
                this.$message({
                  message: response.data.message,
                  type: 'error'
                })
                this.dialogAdd = false
                this.loading_add = false
                this.getDepartment()
                this.resetDialog()
              }
            })
            .catch((err) => {
              this.loading_add = false
              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss">
.el-dialog {
  margin-top: 150px !important;
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

.el-dialog__body {
  padding-top: 10px ;
}

.el-dialog__title {
  font-weight: 500 !important;
}
</style>
