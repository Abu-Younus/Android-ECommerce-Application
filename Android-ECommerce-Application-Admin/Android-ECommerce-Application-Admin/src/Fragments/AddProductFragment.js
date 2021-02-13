import { Backdrop, Box, Button, CircularProgress, FormControl, FormControlLabel, IconButton, Radio, RadioGroup, Snackbar, Switch, TextField, Typography } from '@material-ui/core'
import { Delete, FormatColorFill } from '@material-ui/icons'
import { AddBox, ArrowDownward, Check, ChevronLeft, ChevronRight, Clear, DeleteOutline, Edit, FilterList, FirstPage, Home, LastPage, Remove, SaveAlt, Search, ViewColumn } from '@material-ui/icons';
import MaterialTable from 'material-table'
import React, { Component, forwardRef } from 'react'
import firebase, { firebaseFirestore, storageRef } from '../firebase';

const tableIcons = {
    Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
    Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
    Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
    DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
    Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
    Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
    FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
    LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
    NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
    ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
    SortArrow: forwardRef((props, ref) => <ArrowDownward {...props} ref={ref} />),
    ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
    ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
  };

class AddProductFragment extends Component {

    constructor(props) {
        super(props)
    
        this.state = {
            loading:false,
            snackbar:"",
            images: [],
            coupon_type:"percentage",
            COD:false,
            useTabLayout:false,
            product_title:{error:"",value:""}, 
            product_price:{error:"",value:""},
            cutted_price:{error:"",value:"0"},
            free_coupons:{error:"",value:"0"},
            coupon_title:{error:"",value:""},
            validity_period:{error:"",value:""},
            coupon_body:{error:"",value:""},
            lower_limit:{error:"",value:""},
            upper_limit:{error:"",value:""},
            percentage:{error:"",value:""},
            discount_amount:{error:"",value:""},
            max_quantity:{error:"",value:""},
            stock_quantity:{error:"",value:""},
            offers_applied:{error:"",value:"0"},
            product_description:{error:"",value:""},
            other_details:{error:"",value:""},
            tags:{error:"",value:""},
            columns:[
                { title: 'Key', field: 'field' },
                { title: 'Value', field: 'value' },
              ],
              data:[
                
              ]   
        }
    }
    

    removeImage = index=> {
        let images = this.state.images
        let image = images[index]
        images.splice(index,1)
        try {
            if(image.startsWith("https")) {
                // this.setState({loading:true},
                //     this.deleteImages([image],0,()=>{
                //         this.setState({
                //             loading:false,
                //             images,
                //         })
                //     }))
            }
        } catch (error) {
            this.setState({
                images,
            })  
        }
    }

    renderImageUrl = (item) => {
        try {
            return URL.createObjectURL(item)
        } catch (error) {
            return item
        }
    }

    onChange = (e) => {
        this.setState({
            [e.target.name]:{error:"",value:e.target.value},
        })
    }

    uploadImages = (images,index,Urls,onCompleted) => {
        const uploadAgain = (images,index,Urls,onCompleted) => 
                this.uploadImages(images,index,Urls,onCompleted)
        let file = images[index]

        try {
            if(file.startsWith("https")) {
                Urls.push(file)
                index++
                if(index < images.length) {
                    uploadAgain(images,index,Urls,onCompleted)
                } else {
                    onCompleted()
                }
            }
        } catch (error) {
        var ts = String(new Date().getTime()),
            i = 0,
            out = '';
        
        for (i = 0; i < ts.length; i += 2) {
            out += Number(ts.substr(i, 2)).toString(36);
        }
        let filename = "product" + out;

        var uploadTask = storageRef.child("products/"+filename+".jpg").put(file);
        uploadTask.on('state_changed', function(snapshot){
            var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
            console.log('Upload is ' + progress + '% done');
        }, function(error) {

        }, function() {
                uploadTask.snapshot.ref.getDownloadURL().then((downloadURL)=>{
                    Urls.push(downloadURL)
                    index++
                    if(index < images.length) {
                        uploadAgain(images,index,Urls,onCompleted)
                    } else {
                        onCompleted()
                    }
                });
        });
    }
    }

    uploadProduct = (e) => {
        if(this.state.images.length === 0) {
            this.setState({
                snackbar:"Images is required!"
            })
            return
        }
        if(this.state.useTabLayout && this.state.data.length === 0) {
            return
        }
        let mandatoryFields = [
            "product_title",
            "product_price",
            "max_quantity",
            "stock_quantity",
            "product_description",
            "other_details",
            "tags",
        ]
        if(this.state.attachCoupon) {
            let couponsFields = [
                "coupon_title",
                "coupon_body",
                "validity_period",
                "lower_limit",
                "upper_limit",
            ]
            if(this.state.coupon_type==="percentage") {
                couponsFields.push("percentage")
            } else {
                couponsFields.push("discount_amount")
            }
            mandatoryFields = [...mandatoryFields,...couponsFields]
        }
        let uploadSignal = true
        mandatoryFields.forEach(element => {
            let field = this.state[element]
            if(field.value==="") {
                field.error = "Required!"
                uploadSignal = false
            }
        });
        if(!uploadSignal) {
            this.setState({})
            return
        }

        let index = 0
        let Urls = []
        this.setState({
            loading: true,
        })
        this.uploadImages(this.state.images,index,Urls,()=>{
            let data = {
                added_on:firebase.firestore.Timestamp.fromDate(new Date()),
                ["1_star"]:parseInt(0),
                ["2_star"]:parseInt(0),
                ["3_star"]:parseInt(0),
                ["4_star"]:parseInt(0),
                ["5_star"]:parseInt(0),
                no_of_product_images:Urls.length,
                product_title:this.state.product_title.value,
                product_price:this.state.product_price.value,
                cutted_price:this.state.cutted_price.value,
                free_coupons:parseInt(this.state.free_coupons.value),
                max_quantity:parseInt(this.state.max_quantity.value),
                stock_quantity:parseInt(this.state.stock_quantity.value),
                product_description:this.state.product_description.value,
                product_other_details:this.state.other_details.value,
                offers_applied:parseInt(this.state.offers_applied.value),
                COD:this.state.COD,
                use_tab_layout:this.state.useTabLayout,
                average_ratings:"",
                total_ratings:parseInt(0),
                tags:this.state.tags.value.split(","),
            }
            if(this.state.attachCoupon) {
                data["free_coupons_title"] = this.state.coupon_title.value
                data["free_coupons_body"] = this.state.coupon_body.value
                data["lower_limit"] = this.state.lower_limit.value
                data["upper_limit"] = this.state.upper_limit.value
                data["validity"] = parseInt(this.state.validity_period.value)
                if(this.state.coupon_type==="percentage") {
                    data["percentage"] = this.state.percentage.value
                } else {
                    data["amount"] = this.state.discount_amount.value
                }
            }
            if(this.state.useTabLayout) {
                let sectionCount = 0
                let index = 0
                this.state.data.forEach((row) => {
                    if(row.field === "title") {
                        if(sectionCount>0) {
                            data["spec_title_"+sectionCount+"_total_fields"] = index
                        }
                        index = 0
                        sectionCount++
                        data["spec_title_"+sectionCount] = row.value
                    } else {
                        index++
                        data["spec_title_"+sectionCount+"_field_"+index+"_name"] = row.field
                        data["spec_title_"+sectionCount+"_field_"+index+"_value"] = row.value
                    }
                    data["total_spec_titles"] = sectionCount
                });
            }
            Urls.forEach((url,index) => {
                data["product_image_"+(index+1)] = url
            });

            firebaseFirestore.collection("PRODUCTS")
            .add(data)
            .then((doc)=>{
                let docId = doc.id
                console.log(docId)
                this.setState({
                    loading:false,
                    images: [],
                    coupon_type:"percentage",
                    COD:false,
                    useTabLayout:false,
                    product_title:"", 
                    product_price:"",
                    cutted_price:0,
                    free_coupons:0,
                    coupon_title:"",
                    validity_period:"",
                    coupon_body:"",
                    lower_limit:"",
                    upper_limit:"",
                    percentage:"",
                    discount_amount:"",
                    max_quantity:"",
                    stock_quantity:"",
                    offers_applied:0,
                    product_description:"",
                    other_details:"",
                    tags:"",
                })
            }).catch(err=>{
                this.setState({
                    loading: false,
                })
            })
        })
    }

    render() {
        return (
            <Box bgcolor="#ffffff" boxShadow={1} padding={4}>
                <Typography variant="h5" gutterBottom>
                    New Product
                </Typography>
                <Box display="flex" flexWrap="true">
                        {this.state.images.map((item,index)=>
                        <Box margin="12px">
                            <img src={this.renderImageUrl(item)} 
                                style={{
                                    width:"160px", 
                                    height:"90px", 
                                    objectFit:"scale-down", }} />
                            <br/>
                                <IconButton aria-label="delete"
                                    onClick={e=>this.removeImage(index)}>
                                    <Delete />
                                </IconButton>
                        </Box>)}
                    </Box>
                <input
                    accept="image/*"
                    id="contained-button-file"
                    onChange={e=>{
                    if(e.target.files && e.target.files[0]) {
                        let images = this.state.images
                        images.push(e.target.files[0])
                        this.setState({
                            images,
                        })
                        e.target.value = null
                    }
                    }}
                    hidden
                    name="images"
                    type="file"
                />
                <br/>
                {this.state.images.length < 8 ? 
                <label htmlFor="contained-button-file">
                    <Button variant="contained" color="primary" component="span">
                        Add Image
                    </Button>
                </label> : null 
                }
                <br/>
                <TextField
                    fullWidth
                    margin="normal"
                    label="Product Title"
                    id="outlined-size-small"
                    onChange={this.onChange}
                    name="product_title"
                    defaultValue={this.state.product_title.value}
                    error={this.state.product_title.error!==""}
                    helperText={this.state.product_title.error}
                    variant="outlined"
                    size="small"
                />
                <br/>
                <TextField
                    margin="normal"
                    style={{marginRight:"20px"}}
                    label="Product Price"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="product_price"
                    defaultValue={this.state.product_price.value}
                    error={this.state.product_price.error!==""}
                    helperText={this.state.product_price.error}
                    variant="outlined"
                    size="small"
                />
                <TextField
                    margin="normal"
                    style={{marginLeft:"20px", marginRight:"20px"}}
                    label="Cutted Price"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="cutted_price"
                    defaultValue={this.state.cutted_price.value}
                    error={this.state.cutted_price.error!==""}
                    helperText={this.state.cutted_price.error}
                    variant="outlined"
                    size="small"
                />
                <TextField
                    margin="normal"
                    style={{marginLeft:"20px"}}
                    label="Free Coupons"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="free_coupons"
                    defaultValue={this.state.free_coupons.value}
                    error={this.state.free_coupons.error!==""}
                    helperText={this.state.free_coupons.error}
                    variant="outlined"
                    size="small"
                />
                <br/>
                <FormControlLabel
                control={
                    <Switch
                        name="attach_coupon"
                        color="primary"
                        onChange={e=>this.setState({
                            attachCoupon:e.target.checked,
                        })}
                    />
                }
                label="Attach Coupon"
                />
                <Box border={1} padding={2} borderRadius={16} hidden={!this.state.attachCoupon}>
                <RadioGroup row aria-label="position" name="coupon_type" defaultValue="percentage"
                onChange={e=>this.setState({
                    coupon_type:e.target.value
                })}>
                <FormControlLabel
                    style={{marginRight:"15px"}}
                    value="percentage"
                    control={<Radio color="primary" />}
                    label="Discount"
                    labelPlacement="bottom"
                />
                <FormControlLabel
                    style={{marginLeft:"15px"}}
                    value="flat_bdt_off"
                    control={<Radio color="primary" />}
                    label="Flat BDT. OFF"
                    labelPlacement="bottom"
                />
                </RadioGroup>
                <br/>
                <TextField
                    style={{marginRight:"20px"}}
                    margin="normal"
                    label="Coupon Title"
                    id="outlined-size-small"
                    onChange={this.onChange}
                    name="coupon_title"
                    defaultValue={this.state.coupon_title.value}
                    error={this.state.coupon_title.error!==""}
                    helperText={this.state.coupon_title.error}
                    variant="outlined"
                    size="small"
                />
                <TextField
                    style={{marginLeft:"20px", marginRight:"20px"}}
                    margin="normal"
                    label="Valid for Days"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="validity_period"
                    defaultValue={this.state.validity_period.value}
                    error={this.state.validity_period.error!==""}
                    helperText={this.state.validity_period.error}
                    variant="outlined"
                    size="small"
                />
                <br/>
                <TextField
                    fullWidth
                    margin="normal"
                    id="outlined-multiline-static"
                    label="Coupon Body"
                    onChange={this.onChange}
                    name="coupon_body"
                    defaultValue={this.state.coupon_body.value}
                    error={this.state.coupon_body.error!==""}
                    helperText={this.state.coupon_body.error}
                    multiline
                    rows={4}
                    variant="outlined"
                />
                 <br/>
                <TextField
                    style={{marginRight:"20px"}}
                    margin="normal"
                    label="Lower Limit"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="lower_limit"
                    defaultValue={this.state.lower_limit.value}
                    error={this.state.lower_limit.error!==""}
                    helperText={this.state.lower_limit.error}
                    variant="outlined"
                    size="small"
                />
                <TextField
                    style={{marginLeft:"20px", marginRight:"20px"}}
                    margin="normal"
                    label="Upper Limit"
                    type="number"
                    id="outlined-size-small"
                    onChange={this.onChange}
                    name="upper_limit"
                    defaultValue={this.state.upper_limit.value}
                    error={this.state.upper_limit.error!==""}
                    helperText={this.state.upper_limit.error}
                    variant="outlined"
                    size="small"
                />
                {this.state.coupon_type==="percentage" ?
                <TextField
                    style={{marginLeft:"20px"}}
                    margin="normal"
                    label="Percentage"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="percentage"
                    defaultValue={this.state.percentage.value}
                    error={this.state.percentage.error!==""}
                    helperText={this.state.percentage.error}
                    variant="outlined"
                    size="small"
                />
                :
                <TextField
                    style={{marginRight:"20px"}}
                    margin="normal"
                    label="Discount Amount"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="discount_amount"
                    defaultValue={this.state.discount_amount.value}
                    error={this.state.discount_amount.error!==""}
                    helperText={this.state.discount_amount.error}
                    variant="outlined"
                    size="small"
                />
                }
                </Box>
                <br/>
                <TextField
                    style={{marginRight:"20px"}}
                    margin="normal"
                    label="Max Quantity"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="max_quantity"
                    defaultValue={this.state.max_quantity.value}
                    error={this.state.max_quantity.error!==""}
                    helperText={this.state.max_quantity.error}
                    variant="outlined"
                    size="small"
                />
                <TextField
                    style={{marginLeft:"20px", marginRight:"20px"}}
                    margin="normal"
                    label="Stock Quantity"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="stock_quantity"
                    defaultValue={this.state.stock_quantity.value}
                    error={this.state.stock_quantity.error!==""}
                    helperText={this.state.stock_quantity.error}
                    variant="outlined"
                    size="small"
                />
                <TextField
                    style={{marginLeft:"20px"}}
                    margin="normal"
                    label="Offers Applied"
                    id="outlined-size-small"
                    type="number"
                    onChange={this.onChange}
                    name="offers_applied"
                    defaultValue={this.state.offers_applied.value}
                    error={this.state.offers_applied.error!==""}
                    helperText={this.state.offers_applied.error}
                    variant="outlined"
                    size="small"
                />
                <br/>
                <TextField
                    fullWidth
                    margin="normal"
                    id="outlined-multiline-static"
                    label="Product Description"
                    onChange={this.onChange}
                    name="product_description"
                    defaultValue={this.state.product_description.value}
                    error={this.state.product_description.error!==""}
                    helperText={this.state.product_description.error}
                    multiline
                    rows={4}
                    variant="outlined"
                />
                <br/>
                <FormControlLabel
                control={
                    <Switch
                        name="use_tab_layout"
                        color="primary"
                        onChange={e=>this.setState({
                            useTabLayout:e.target.checked,
                        })}
                    />
                }
                label="Use Tab Layout"
                />
                <br/>
                {this.state.useTabLayout &&
                <MaterialTable
                        icons={tableIcons}
                        title="Product Specifications"
                        options={{search:false,paging:false}}
                        columns={this.state.columns}
                        data={this.state.data}
                        editable={{
                            onRowAdd: (newData) =>
                            new Promise((resolve) => {
                                setTimeout(() => {
                                    resolve()
                                    this.setState((prevState)=>{
                                        const data = [...prevState.data]
                                        data.push(newData)
                                        return {...prevState, data}
                                    })
                                }, 1000)
                            }),
                            onRowUpdate: (newData, oldData) =>
                            new Promise((resolve) => {
                                setTimeout(() => {
                                    resolve();
                                    this.setState((prevState)=>{
                                        const data = [...prevState.data]
                                        data[data.indexOf(oldData)] = newData
                                        return {...prevState, data}
                                    })
                                }, 1000)
                            }),  
                            onRowDelete: (oldData) =>
                            new Promise((resolve)=>{
                                setTimeout(() => {
                                    resolve();
                                    this.setState((prevState)=>{
                                        const data = [...prevState.data]
                                        data.splice(data.indexOf(oldData),1)
                                        return {...prevState, data}
                                    })
                                }, 1000)
                            }),
                        }}
                    /> }
                    <br/>
                <TextField
                    fullWidth
                    margin="normal"
                    id="outlined-multiline-static"
                    label="Other Details"
                    onChange={this.onChange}
                    name="other_details"
                    defaultValue={this.state.other_details.value}
                    error={this.state.other_details.error!==""}
                    helperText={this.state.other_details.error}
                    multiline
                    rows={4}
                    variant="outlined"
                />
                <br/><br/>
                <FormControlLabel
                control={
                    <Switch
                        name="COD"
                        color="primary"
                        onChange={e=>this.setState({
                            COD:e.target.checked,
                        })}
                    />
                }
                label="COD"
                />
                <br/>
                <TextField
                    fullWidth
                    margin="normal"
                    label="Tags"
                    id="outlined-size-small"
                    onChange={this.onChange}
                    name="tags"
                    defaultValue={this.state.tags.value}
                    error={this.state.tags.error!==""}
                    helperText={this.state.tags.error}
                    variant="outlined"
                    size="small"
                />
                <br/><br/>
                 <Button variant="contained" fullWidth color="primary" component="span"
                 onClick={this.uploadProduct}>
                    Upload Product Info
                </Button>
                <Backdrop style={{zIndex:"1500"}} open={this.state.loading}>
                    <CircularProgress color="primary" />
              </Backdrop>
              <Snackbar
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                }}
                open={this.state.snackbar !== ""}
                autoHideDuration={6000}
                onClose={e => this.setState({
                    snackbar:"",
                })}
                message={this.state.snackbar}
              />
            </Box>
        )
    }
}

export default AddProductFragment
